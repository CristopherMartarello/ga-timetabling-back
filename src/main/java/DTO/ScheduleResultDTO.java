package DTO;

import model.ObjetoTabela;

public class ScheduleResultDTO {
    //ARMAZENA OS DADOS QUE SERÃO ENVIADOS PELO FRONT
    private int bestFitnessScore;
    private ObjetoTabela[] objTabela;
    private int contIteracoes;
    private int IteracoesTotal;
    private long tempoExecucao;

    
    public int getBestFitnessScore() {
        return bestFitnessScore;
    }

    public void setBestFitnessScore(int bestFitnessScore) {
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

    public int getIteracoesTotal() {
        return IteracoesTotal;
    }

    public void setIteracoesTotal(int IteracoesTotal) {
        this.IteracoesTotal = IteracoesTotal;
    }
    
    public long getTempoExecucao() {
        return tempoExecucao;
    }

    public void setTempoExecucao(long tempoExecucao) {
        this.tempoExecucao = tempoExecucao;
    }
    

}
