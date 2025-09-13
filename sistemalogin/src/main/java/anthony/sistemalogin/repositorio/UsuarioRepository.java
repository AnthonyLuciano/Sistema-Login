package anthony.sistemalogin.repositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import anthony.sistemalogin.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca um usuário pelo nome de usuário
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByUsernameContainingIgnoreCase(String username);
    List<Usuario> findByAtivoTrue();
    List<Usuario> findByUsernameContainingIgnoreCaseAndAtivoTrue(String username);
}
