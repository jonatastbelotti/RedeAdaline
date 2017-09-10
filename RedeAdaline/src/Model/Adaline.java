package Model;

import View.Comunicador;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Jônatas Trabuco Belotti [jonatas.t.belotti@hotmail.com]
 */
public class Adaline {

  public static final int NUM_SINAIS_ENTRADA = 4;
  private final double TAXA_APRENDIZAGEM = 0.0025;
  private final double PRECISAO = 0.000001;

  private double peso0;
  private double peso1;
  private double peso2;
  private double peso3;
  private double peso4;
  private double ultimaResposta;
  private int numEpocasTreinamento;

  public Adaline() {
    Random random = new Random();

    this.peso0 = random.nextDouble();
    this.peso1 = random.nextDouble();
    this.peso2 = random.nextDouble();
    this.peso3 = random.nextDouble();
    this.peso4 = random.nextDouble();
    this.ultimaResposta = 0.0;
    this.numEpocasTreinamento = 0;
  }

  public Adaline(double peso0, double peso1, double peso2, double peso3, double peso4) {
    this.peso0 = peso0;
    this.peso1 = peso1;
    this.peso2 = peso2;
    this.peso3 = peso3;
    this.peso4 = peso4;
    this.ultimaResposta = 0.0;
    this.numEpocasTreinamento = 0;
  }

  public double getUltimaResposta() {
    return ultimaResposta;
  }

  public int getNumEpocasTreinamento() {
    return numEpocasTreinamento;
  }

  public double getPeso0() {
    return peso0;
  }

  public double getPeso1() {
    return peso1;
  }

  public double getPeso2() {
    return peso2;
  }

  public double getPeso3() {
    return peso3;
  }

  public double getPeso4() {
    return peso4;
  }

  public void setPeso4(double peso4) {
    this.peso4 = peso4;
  }

  public boolean treinarRede(ArquivoDadosTreinamento arquivoTreinamento) {
    FileReader arq;
    BufferedReader lerArq;
    String linha;
    String[] vetor;
    int i;
    double entrada0;
    double entrada1;
    double entrada2;
    double entrada3;
    double entrada4;
    double saidaEsperada;
    double erroAnterior;
    double erroAtual;
    double menorErro;
    
    this.numEpocasTreinamento = 0;
    menorErro = Double.MAX_VALUE;

    Comunicador.iniciarLog("Início treinamento");
    imprimirSituacao(arquivoTreinamento);
    
    try {
      do {
        this.numEpocasTreinamento++;
        erroAnterior = getErro(arquivoTreinamento);
        arq = new FileReader(arquivoTreinamento.getCaminhoCompleto());
        lerArq = new BufferedReader(arq);

        linha = lerArq.readLine();
        if (linha.contains("x1")) {
          linha = lerArq.readLine();
        }

        while (linha != null) {
          vetor = linha.split("\\s+");
          i = 0;

          if (vetor[0].equals("")) {
            i = 1;
          }

          entrada0 = -1.0;
          entrada1 = Double.parseDouble(vetor[i++].replace(",", "."));
          entrada2 = Double.parseDouble(vetor[i++].replace(",", "."));
          entrada3 = Double.parseDouble(vetor[i++].replace(",", "."));
          entrada4 = Double.parseDouble(vetor[i++].replace(",", "."));
          saidaEsperada = Double.parseDouble(vetor[i].replace(",", "."));

          this.ultimaResposta = (entrada1 * this.peso1) + (entrada2 * this.peso2) + (entrada3 * this.peso3) + (entrada4 * this.peso4) + (entrada0 * this.peso0);

          this.peso0 = peso0 + (TAXA_APRENDIZAGEM * (saidaEsperada - this.ultimaResposta) * entrada0);
          this.peso1 = peso1 + (TAXA_APRENDIZAGEM * (saidaEsperada - this.ultimaResposta) * entrada1);
          this.peso2 = peso2 + (TAXA_APRENDIZAGEM * (saidaEsperada - this.ultimaResposta) * entrada2);
          this.peso3 = peso3 + (TAXA_APRENDIZAGEM * (saidaEsperada - this.ultimaResposta) * entrada3);
          this.peso4 = peso4 + (TAXA_APRENDIZAGEM * (saidaEsperada - this.ultimaResposta) * entrada4);

          linha = lerArq.readLine();
        }

        arq.close();
        erroAtual = getErro(arquivoTreinamento);
        if (erroAtual < menorErro) {
          menorErro = erroAtual;
          imprimirSituacao(arquivoTreinamento);
        }

      } while (Math.abs(erroAtual - erroAnterior) > PRECISAO);

      Comunicador.addLog("\nFim do treinamento. Numero de epocas: " + this.numEpocasTreinamento);
    } catch (FileNotFoundException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }

    return true;
  }

  public String classificar(double valor1, double valor2, double valor3, double valor4) {
    String resposta;

    resposta = "Sem classificação";

    this.ultimaResposta = funcaoDegrauBipolar((valor1 * this.peso1) + (valor2 * this.peso2) + (valor3 * this.peso3) + (valor4 * this.peso4) + (-1.0 * this.peso0));

    if (this.ultimaResposta == -1.0) {
      resposta = "Classe A";
    }
    if (this.ultimaResposta == 1.0) {
      resposta = "Classe B";
    }

    return resposta;
  }

  private double getErro(ArquivoDadosTreinamento arquivoTreinamento) {
    FileReader arq;
    BufferedReader lerArq;
    String linha;
    String[] vetor;
    int i;
    int numDadosTreinamento;
    double entrada1;
    double entrada2;
    double entrada3;
    double entrada4;
    double saidaEsperada;
    double erro;

    try {
      erro = 0D;
      numDadosTreinamento = 0;
      arq = new FileReader(arquivoTreinamento.getCaminhoCompleto());
      lerArq = new BufferedReader(arq);

      linha = lerArq.readLine();
      if (linha.contains("x1")) {
        linha = lerArq.readLine();
      }

      while (linha != null) {
        numDadosTreinamento++;
        vetor = linha.split("\\s+");
        i = 0;

        if (vetor[0].equals("")) {
          i = 1;
        }

        entrada1 = Double.parseDouble(vetor[i++].replace(",", "."));
        entrada2 = Double.parseDouble(vetor[i++].replace(",", "."));
        entrada3 = Double.parseDouble(vetor[i++].replace(",", "."));
        entrada4 = Double.parseDouble(vetor[i++].replace(",", "."));
        saidaEsperada = Double.parseDouble(vetor[i].replace(",", "."));

        this.ultimaResposta = (entrada1 * this.peso1) + (entrada2 * this.peso2) + (entrada3 * this.peso3) + (entrada4 * this.peso4) + (-1.0 * this.peso0);
        erro = erro + Math.pow((saidaEsperada - this.ultimaResposta), 2.0);

        linha = lerArq.readLine();
      }

      arq.close();
    } catch(IOException | NumberFormatException e) {
      return 0D;
    }
    
    erro = erro / numDadosTreinamento;

    return erro;
  }

  private double funcaoDegrauBipolar(double valor) {
    if (valor < 0.0) {
      return -1.0;
    }

    if (valor >= 0.0) {
      return 1.0;
    }

    return 0.0;
  }
  
  private void imprimirSituacao(ArquivoDadosTreinamento arquivoTreinamento) {
    Comunicador.addLog("EPOCA " + this.numEpocasTreinamento + " -----------------");
    Comunicador.addLog("Erro: " + Double.toString(getErro(arquivoTreinamento)).replace(".", ","));
    Comunicador.addLog("Pesos: " + Double.toString(this.peso0).replace(".", ",") + "; " + Double.toString(this.peso1).replace(".", ",")
            + "; " + Double.toString(this.peso2).replace(".", ",") + "; " + Double.toString(this.peso3).replace(".", ",")
            + "; " + Double.toString(this.peso4).replace(".", ","));
  }

}

