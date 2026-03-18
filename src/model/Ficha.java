package model;

import java.util.ArrayList;
import java.util.List;

public class Ficha {
    private int id;
    private int alunoId;
    private String semana;
    private String observacoesMedicas;
    private String nomeAlunoTemporario;
    private double peso;
    private double percentualGordura;
    private double massaMagra;
    private List<ItemFicha> itens = new ArrayList<>();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getAlunoId() {
        return alunoId;
    }
    public void setAlunoId(int alunoId) {
        this.alunoId = alunoId;
    }
    public String getSemana() {
        return semana;
    }
    public void setSemana(String semana) {
        this.semana = semana;
    }
    public String getObservacoesMedicas() {
        return observacoesMedicas;
    }
    public void setObservacoesMedicas(String observacoesMedicas) {
        this.observacoesMedicas = observacoesMedicas;
    }
    public List<ItemFicha> getItens() {
        return itens;
    }
    public void adicionarItem(ItemFicha item) {
        this.itens.add(item);
    }

    public String getNomeAlunoTemporario() {
        return nomeAlunoTemporario;
    }

    public void setNomeAlunoTemporario(String nomeAlunoTemporario) {
        this.nomeAlunoTemporario = nomeAlunoTemporario;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPercentualGordura() {
        return percentualGordura;
    }

    public void setPercentualGordura(double percentualGordura) {
        this.percentualGordura = percentualGordura;
    }

    public double getMassaMagra() {
        return massaMagra;
    }

    public void setMassaMagra(double massaMagra) {
        this.massaMagra = massaMagra;
    }
}