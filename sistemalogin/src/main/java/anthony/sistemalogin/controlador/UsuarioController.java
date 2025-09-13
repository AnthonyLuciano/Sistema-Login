package anthony.sistemalogin.controlador;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import anthony.sistemalogin.modelo.Usuario;
import anthony.sistemalogin.repositorio.UsuarioRepository;
import anthony.sistemalogin.servico.ServicoEmail;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicoEmail emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent() && passwordEncoder.matches(password, usuarioOpt.get().getPassword())) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.isEmailVerificado()) {
                model.addAttribute("username", usuario.getUsername());
                model.addAttribute("erro", "Você precisa confirmar seu e-mail para acessar o sistema.");
                return "confirmar-email";
            }
            if (!usuario.isAtivo()) {
                model.addAttribute("erro", "Sua conta está inativa. Entre em contato com o suporte.");
                return "login";
            }
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:/home";
        }
        model.addAttribute("erro", "Usuário ou senha inválidos");
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastro(@RequestParam String username, @RequestParam String email,
                       @RequestParam String password, Model model) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            model.addAttribute("erro", "Usuário já existe");
            return "cadastro";
        }
        String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmailVerificado(false);
        usuario.setCodigoVerificacao(codigo);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        emailService.enviarCodigoVerificacao(email, codigo);

        model.addAttribute("username", username);
        return "confirmar-email";
    }

    @GetMapping("/confirmar-email")
    public String confirmarEmailForm(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "confirmar-email";
    }

    @GetMapping("/reenviar-codigo")
    public String reenviarCodigo(@RequestParam String username, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String novoCodigo = String.format("%06d", new java.util.Random().nextInt(999999));
            usuario.setCodigoVerificacao(novoCodigo);
            usuarioRepository.save(usuario);
            emailService.enviarCodigoVerificacao(usuario.getEmail(), novoCodigo);
            model.addAttribute("mensagem", "Novo código enviado para seu e-mail.");
            model.addAttribute("username", username);
        } else {
            model.addAttribute("erro", "Usuário não encontrado.");
        }
        return "confirmar-email";
    }

    @PostMapping("/confirmar-email")
    public String confirmarEmail(@RequestParam String username, @RequestParam String codigo, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getCodigoVerificacao().equals(codigo)) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEmailVerificado(true);
            usuario.setCodigoVerificacao(null);
            usuarioRepository.save(usuario);
            return "redirect:/login";
        }
        model.addAttribute("erro", "Código inválido");
        model.addAttribute("username", username);
        return "confirmar-email";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "home";
    }
}