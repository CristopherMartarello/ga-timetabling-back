package model;

import java.util.ArrayList;

/**
 *
 * @author Cristopher
 */

public class Professor {
    //CLASSE QUE IRÁ CONTER OS DADOS DOS PROFESSORES
    private final String nome;
    private final ArrayList horarios;

    public Professor(String nome, ArrayList horarios) {
        this.nome = nome;
        this.horarios = horarios;
    }
    
    public String getNome() { return nome; }
    public ArrayList getHorarios() { return horarios; }
}