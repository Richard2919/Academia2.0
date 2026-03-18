package model;

public class ItemFicha {
    private int exercicioId; // Usado para salvar no banco
    private String nomeExercicio; // Usado para exibir para o aluno
    private String diaSemana;
    private int series;
    private int repeticoes;
    private double carga;

    // Construtor usado na hora de CRIAR a ficha (não sabemos o nome ainda, só o ID)
    public ItemFicha(int exercicioId, String diaSemana, int series, int repeticoes, double carga) {
        this.exercicioId = exercicioId;
        this.diaSemana = diaSemana;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }

    // Construtor usado na hora de LER a ficha do banco (traz o nome popular via JOIN)
    public ItemFicha(String diaSemana, String nomeExercicio, int series, int repeticoes, double carga) {
        this.diaSemana = diaSemana;
        this.nomeExercicio = nomeExercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }

    public int getExercicioId() {
        return exercicioId;
    }
    public String getNomeExercicio() {
        return nomeExercicio;
    }
    public String getDiaSemana() {
        return diaSemana;
    }
    public int getSeries() {
        return series;
    }
    public int getRepeticoes() {
        return repeticoes;
    }
    public double getCarga() {
        return carga;
    }
}