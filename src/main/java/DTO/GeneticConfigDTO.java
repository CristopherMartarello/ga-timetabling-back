
package DTO;

public class GeneticConfigDTO {
    private Integer probabilidadeCruzamento;
    private Integer mutacao;
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

    public Integer getMutacao() {
        return mutacao;
    }

    public void setMutacao(Integer mutacao) {
        this.mutacao = mutacao;
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
