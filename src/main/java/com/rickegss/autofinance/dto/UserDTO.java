package com.rickegss.autofinance.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message =  "O email não pode estar vazio")
    @Email(message = "Digite um endereço de email válido")
    private String email;

    @NotBlank(message = "A senha não pode estar vazia")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotBlank(message = "Confirme a senha")
    @Size(min = 6, message = "A confirmação deve ter no mínimo 6 caracteres e ser igual a senha")
    private String confirmPassword;
}
