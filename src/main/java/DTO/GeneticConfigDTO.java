
package DTO;

public class GeneticConfigDTO {
    //CLASSE QUE RECEBERÁ OS DADOS DO FRONT
    private Integer probabilidadeCruzamento;
    private Integer probabilidadeMutacao;
    private Integer qtdElitismo;
    private Integer iteracoes;
    private Integer iteracoesSemMelhoria;

    // Getters e Setters
    public Integer getProbabilidadeCruzamento() {
        return probabilidadeCruzamento;
    }

    public void setProbabilidadeCruzamento(Integer probabilidadeCruzamento) {
        this.probabilidadeCruzamento = probabilidadeCruzamento;
    }

    public Integer getProbabilidadeMutacao() {
        return probabilidadeMutacao;
    }

    public void setProbabilidadeMutacao(Integer probabilidadeMutacao) {
        this.probabilidadeMutacao = probabilidadeMutacao;
    }

    public Integer getQtdElitismo() {
        return qtdElitismo;
    }

    public void setQtdElitismo(Integer qtdElitismo) {
        this.qtdElitismo = qtdElitismo;
    }

    public Integer getIteracoes() {
        return iteracoes;
    }

    public void setIteracoes(Integer iteracoes) {
        this.iteracoes = iteracoes;
    }

    public Integer getIteracoesSemMelhoria() {
        return iteracoesSemMelhoria;
    }

    public void setIteracoesSemMelhoria(Integer iteracoesSemMelhoria) {
        this.iteracoesSemMelhoria = iteracoesSemMelhoria;
    }
}
