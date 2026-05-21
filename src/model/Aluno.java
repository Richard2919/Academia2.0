package model;

import java.time.LocalDate;

public class Aluno {
    private int id;
    private String nome;
    private String cpf;
    private LocalDate dataVencimento;

    // Construtor vazio (importante para frameworks e para o DAO)
    public Aluno() {}

    public Aluno(String nome, String cpf,LocalDate dataVencimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataVencimento = dataVencimento;
    }

    public Aluno(String nome, String cpf) {
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