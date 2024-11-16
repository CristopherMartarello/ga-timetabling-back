package DTO;

import java.util.ArrayList;
import java.util.Map;
import model.ObjetoTabela;

public class ScheduleResultDTO {

    private ArrayList<Integer> bestFitnessScore;
    private ObjetoTabela[] objTabela;
    private int contIteracoes;
    private long tempoExecucao;

    // Getters e Setters
    public ArrayList<Integer> getBestFitnessScore() {
        return bestFitnessScore;
    }

    public void setBestFitnessScore(ArrayList<Integer> bestFitnessScore) {
        this.bestFitnessScore = bestFitnessScore;
    }

    public ObjetoTabela[] getObjTabela() {
        return objTabela;
    }

    public void setObjTabela(ObjetoTabela[] objTabela) {
        this.objTabela = objTabela;
    }

    public int getContIteracoes() {
        return contIteracoes;
    }

    public void setContIteracoes(int contIteracoes) {
        this.contIteracoes = contIteracoes;
    }

    public long getTempoExecucao() {
        return tempoExecucao;
    }

    public void setTempoExecucao(long tempoExecucao) {
        this.tempoExecucao = tempoExecucao;
    }
    

}
