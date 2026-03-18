package model;

public class Exercicio {
    private int id;
    private String nomeTecnico;
    private String nomePopular;
    private String parteCorpo;

    public Exercicio() {}
    public Exercicio(String nomeTecnico, String nomePopular, String parteCorpo) {
        this.nomeTecnico = nomeTecnico;
        this.nomePopular = nomePopular;
        this.parteCorpo = parteCorpo;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNomeTecnico() {
        return nomeTecnico;
    }
    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }
    public String getNomePopular() {
        return nomePopular;
    }
    public void setNomePopular(String nomePopular) {
        this.nomePopular = nomePopular;
    }
    public String getParteCorpo() {
        return parteCorpo;
    }
    public void setParteCorpo(String parteCorpo) {
        this.parteCorpo = parteCorpo;
    }
}