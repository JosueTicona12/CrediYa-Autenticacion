package co.com.autenticacion.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


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

    private String nombres;

    private String apellidos;

    @Column("documento_identidad")
    private String numDocumento;

    @Column("fech_nacimiento")
    private LocalDate nacimiento;

    private String direccion;

    @Column("password")
    private String password;

    private String telefono;

    private String email;

    private Integer salario;

    @Column("id_rol") // FK a la tabla Rol
    private Long rolId;

    private Long activo;
}
