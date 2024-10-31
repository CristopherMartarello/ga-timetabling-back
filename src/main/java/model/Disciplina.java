package model;

/**
 *
 * @author Cristopher
 */

public class Disciplina {
    
    private int codigo;
    private String nome;
    private int fase;
    private int cargaHoraria;
    private String professor;

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