package model;

import java.time.LocalDate;

public class Aluno {
    private int id;
    private String nome;
    private LocalDate dataVencimento; // Substituímos o boolean por data

    public Aluno() {}
    public Aluno(String nome, LocalDate dataVencimento) {
        this.nome = nome;
        this.dataVencimento = dataVencimento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    // REGRA DE NEGÓCIO: O próprio aluno sabe dizer se está devendo
    public boolean isPagamentoAtrasado() {
        // Se a data de vencimento for ANTES da data de hoje, está atrasado
        return dataVencimento.isBefore(LocalDate.now());
    }
}