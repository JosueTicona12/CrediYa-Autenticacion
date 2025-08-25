package co.com.autenticacion.model.usuario;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {
    private Long id;
    private String nombres;
    private String apellidos;
    private String numDocumento;
    private LocalDate nacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private Integer salario;
    private Long rolId;
    private Long activo;


}
