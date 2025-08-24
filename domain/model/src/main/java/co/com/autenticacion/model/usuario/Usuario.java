package co.com.autenticacion.model.usuario;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {
    private String id;
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer tipoId;
    private Long numeroId;


}
