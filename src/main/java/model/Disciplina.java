package model;

/**
 *
 * @author Cristopher
 */

public class Disciplina {
    //CLASSE QUE IRÁ CONTER OS DADOS DE CADA DISCIPLINA
    private final int codigo;
    private final String nome;
    private final int fase;
    private final int cargaHoraria;
    private final String professor;

    public Disciplina(int codigo, String nome, int fase, int cargaHoraria, String professor) {
        this.codigo = codigo;
        this.nome = nome;
        this.fase = fase;
        this.cargaHoraria = cargaHoraria;
        this.professor = professor;
    }
    
    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getFase() { return fase; }
    public int getCargaHoraria() { return cargaHoraria; }
    public String getProfessor() { return professor; }
    
}