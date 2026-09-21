package br.com.gerenciamentoteatro.model.enums;

public enum Turno {

    MATUTINO("08:00", "12:00"),
    VESPERTINO("13:00", "18:00"),
    NOTURNO("19:00", "23:00");

    private final String horarioInicio;
    private final String horarioFim;

    Turno(String horarioInicio, String horarioFim) {
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public String getHorarioFim() {
        return horarioFim;
    }
}
