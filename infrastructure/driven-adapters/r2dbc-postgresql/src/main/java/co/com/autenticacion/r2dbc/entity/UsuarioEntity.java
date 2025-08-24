package co.com.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
@Table("usuario")
public class UsuarioEntity {
    @Id
    @Column("id")
    private Long id;

    private String nombre;

    private String apellido;

    private Integer edad;

    private Integer tipoid;

    private Long numeroid;
}
