package me.leogianfagna.emblemas;

public class Emblema {
    private String nome;
    private String categoria;
    private int idConquistado;
    private int idNaoConquistado;
    private String descricaoRapida;
    private String descricaoCompleta;
    private int raridade;
    private String dataLancamento;
    private String localLancamento;
    private String modoConquista;
    private String identificador;

    public Emblema(String nome, String categoria, int idConquistado, int idNaoConquistado, String descricaoRapida,
            String descricaoCompleta, int raridade, String dataLancamento, String localLancamento,
            String modoConquista, String identificador) {
        this.nome = nome;
        this.categoria = categoria;
        this.idConquistado = idConquistado;
        this.idNaoConquistado = idNaoConquistado;
        this.descricaoRapida = descricaoRapida;
        this.descricaoCompleta = descricaoCompleta;
        this.raridade = raridade;
        this.dataLancamento = dataLancamento;
        this.localLancamento = localLancamento;
        this.modoConquista = modoConquista;
        this.identificador = identificador;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public int getCustomModelData() {
        return this.idConquistado;
    }

    public int getCustomBlack() {
        return this.idNaoConquistado;
    }

    public String getDescricaoRapida() {
        return this.descricaoRapida;
    }

    public String getDescricaoCompleta() {
        return this.descricaoCompleta;
    }

    public int getRaridade() {
        return this.raridade;
    }

    public String getDataLancamento() {
        return this.dataLancamento;
    }

    public String getLocalLancamento() {
        return this.localLancamento;
    }

    public String getModoConquista() {
        return this.modoConquista;
    }

    public String getIdentificador() {
        return this.identificador;
    }
}
