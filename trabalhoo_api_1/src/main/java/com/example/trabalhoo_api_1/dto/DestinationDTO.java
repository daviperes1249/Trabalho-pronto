package com.example.trabalhoo_api_1.dto;

//tentei implementar o dto para botar um filtro sla talvez não mostrar o id pro usuario.


public class DestinationDTO {

    private Long id;
    private String nome;
    private String localizacao;

    public DestinationDTO() {
    }

    public DestinationDTO(Long id, String nome, String localizacao) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
