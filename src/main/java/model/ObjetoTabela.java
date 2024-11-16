package model;

/**
 *
 * @author Nathan
 */
public class ObjetoTabela {
    private int[] codigo;
    private String[] nomeMateria;
    private String[] professorMinistrante;

    public ObjetoTabela(int[] codigo, String[] nomeMateria, String[] professorMinistrante) {
        this.codigo = codigo;
        this.nomeMateria = nomeMateria;
        this.professorMinistrante = professorMinistrante;
    }

    public int[] getCodigo() {
        return codigo;
    }

    public void setCodigo(int[] codigo) {
        this.codigo = codigo;
    }

    public String[] getNomeMateria() {
        return nomeMateria;
    }

    public void setNomeMateria(String[] nomeMateria) {
        this.nomeMateria = nomeMateria;
    }

    public String[] getProfessorMinistrante() {
        return professorMinistrante;
    }

    public void setProfessorMinistrante(String[] professorMinistrante) {
        this.professorMinistrante = professorMinistrante;
    }
    
    
}
