package model;

import java.time.LocalDate;

public class Aluno {
    private int id;
    private String nome;
    private String cpf;
    private int altura;
    private LocalDate dataVencimento;

    // Construtor vazio (importante para frameworks e para o DAO)
    public Aluno() {}

    public Aluno(String nome, String cpf, int altura, LocalDate dataVencimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.altura = altura;
        this.dataVencimento = dataVencimento;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getAltura() {
        return altura;
    }
    public void setAltura(int altura) {
        this.altura = altura;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    // --- REGRAS DE NEGÓCIO ---

    public boolean isPagamentoAtrasado() {
        return dataVencimento != null && dataVencimento.isBefore(LocalDate.now());
    }
}