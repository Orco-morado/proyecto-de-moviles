package com.example.proyectoappmovil.data.model

enum class PerfilUsuario(val etiqueta: String) {
    EJECUTIVO("Ejecutivo comercial"),
    SUPERVISOR("Supervisor comercial"),
    MARKETING("Marketing y comunicaciones")
}

enum class EstadoUsuario(val etiqueta: String) {
    ACTIVO("Activo"),
    INACTIVO("Inactivo")
}

enum class TipoOrganizacion(val etiqueta: String) {
    EMPRESA("Empresa"),
    MUNICIPALIDAD("Municipalidad"),
    INSTITUCION_PUBLICA("Institución pública"),
    INSTITUCION_PRIVADA("Institución privada"),
    ORGANIZADOR_EVENTOS("Organizador de eventos"),
    CENTRO_COMERCIAL("Centro comercial"),
    ASOCIACION("Asociación"),
    OTRA("Otra entidad")
}

enum class TipoEvento(val etiqueta: String) {
    EVENTO_CORPORATIVO("Evento corporativo"),
    FERIA_EMPRESARIAL("Feria empresarial"),
    ACTIVIDAD_MUNICIPAL("Actividad municipal"),
    JORNADA_BENEFICIOS("Jornada de beneficios"),
    FERIA_INSTITUCIONAL("Feria institucional"),
    ACTIVIDAD_TRABAJADORES("Actividad de trabajadores"),
    ACTIVACION_COMERCIAL("Activación comercial"),
    OTRA("Otra instancia")
}

enum class EstadoOportunidad(val etiqueta: String) {
    EVENTO_IDENTIFICADO("Evento identificado"),
    CONTACTO_PENDIENTE("Contacto pendiente"),
    CONTACTO_REALIZADO("Contacto realizado"),
    PROPUESTA_ENVIADA("Propuesta enviada"),
    EN_ESPERA_RESPUESTA("En espera de respuesta"),
    PARTICIPACION_CONFIRMADA("Participación confirmada"),
    OPORTUNIDAD_DESCARTADA("Oportunidad descartada"),
    EN_EVALUACION("En evaluación"),
    INFORMACION_INCOMPLETA("Información incompleta"),
    REQUIERE_SEGUIMIENTO("Requiere seguimiento"),
    ACTIVACION_REALIZADA("Activación realizada"),
    ACTIVACION_CANCELADA("Activación cancelada"),
    OPORTUNIDAD_CERRADA("Oportunidad cerrada")
}

enum class Prioridad(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta")
}

enum class AreaContacto(val etiqueta: String) {
    EVENTOS("Eventos"),
    ACTIVACIONES("Activaciones"),
    MARKETING("Marketing"),
    COMUNICACIONES("Comunicaciones"),
    RECURSOS_HUMANOS("Recursos Humanos"),
    BIENESTAR("Bienestar"),
    BENEFICIOS("Beneficios"),
    ADMINISTRACION("Administración")
}

enum class MedioContacto(val etiqueta: String) {
    CORREO("Correo"),
    TELEFONO("Teléfono"),
    REUNION("Reunión"),
    OTRO("Otro")
}

enum class EstadoContacto(val etiqueta: String) {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    SIN_RESPUESTA("Sin respuesta")
}

enum class TipoAccionSeguimiento(val etiqueta: String) {
    INVESTIGACION_INICIAL("Investigación inicial"),
    BUSQUEDA_CONTACTO("Búsqueda de contacto"),
    PRIMER_CONTACTO("Primer contacto"),
    ENVIO_PRESENTACION("Envío de presentación"),
    ENVIO_PROPUESTA("Envío de propuesta"),
    SOLICITUD_ANTECEDENTES("Solicitud de antecedentes"),
    REUNION("Reunión"),
    LLAMADA("Llamada"),
    SEGUIMIENTO("Seguimiento"),
    RECEPCION_RESPUESTA("Recepción de respuesta"),
    CONFIRMACION("Confirmación"),
    CIERRE("Cierre")
}
