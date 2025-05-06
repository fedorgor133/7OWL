package edu.ub.pis2425.projecte7owls.presentation.model;


public class Question {
    private String id;
    private String pregunta;
    private String respuestaCorrecta;
    private String respuesta1;
    private String respuesta2;
    private String respuesta3;
    private boolean contestadaCorrecta;

    public Question() {}

    public Question(String id, String pregunta, String respuestaCorrecta, String respuesta1, String respuesta2, String respuesta3, boolean contestadaCorrecta) {
        this.id = id;
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.respuesta1 = respuesta1;
        this.respuesta2 = respuesta2;
        this.respuesta3 = respuesta3;
        this.contestadaCorrecta = contestadaCorrecta;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }

    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }

    public String getRespuesta1() { return respuesta1; }
    public void setRespuesta1(String respuesta1) { this.respuesta1 = respuesta1; }

    public String getRespuesta2() { return respuesta2; }
    public void setRespuesta2(String respuesta2) { this.respuesta2 = respuesta2; }

    public String getRespuesta3() { return respuesta3; }
    public void setRespuesta3(String respuesta3) { this.respuesta3 = respuesta3; }

    public boolean isContestadaCorrecta() { return contestadaCorrecta; }
    public void setContestadaCorrecta(boolean contestadaCorrecta) { this.contestadaCorrecta = contestadaCorrecta; }
}
