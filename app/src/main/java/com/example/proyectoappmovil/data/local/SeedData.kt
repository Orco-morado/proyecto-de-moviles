package com.example.proyectoappmovil.data.local

import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.local.entity.Evento
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.MedioContacto
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.model.TipoOrganizacion

object SeedData {

    private const val DIA = 24L * 60 * 60 * 1000

    suspend fun cargarSiEstaVacio(baseDeDatos: AppDatabase) {
        if (baseDeDatos.usuarioDao().contar() > 0) return

        val ahora = System.currentTimeMillis()

        val idAna = baseDeDatos.usuarioDao().insertar(
            Usuario(
                nombre = "Ana Torres",
                perfil = PerfilUsuario.EJECUTIVO,
                area = "Comercial",
                zona = "Región Metropolitana",
                estadoCuenta = EstadoUsuario.ACTIVO,
                fechaCreacion = ahora,
                fechaActualizacion = ahora
            )
        )
        val idMarcos = baseDeDatos.usuarioDao().insertar(
            Usuario(
                nombre = "Marcos Rivas",
                perfil = PerfilUsuario.SUPERVISOR,
                area = "Comercial",
                zona = "Región Metropolitana",
                estadoCuenta = EstadoUsuario.ACTIVO,
                fechaCreacion = ahora,
                fechaActualizacion = ahora
            )
        )

        val idMunicipalidad = baseDeDatos.organizacionDao().insertar(
            Organizacion(
                nombre = "Municipalidad de Puente Alto",
                tipo = TipoOrganizacion.MUNICIPALIDAD,
                rubro = "Servicios públicos",
                comuna = "Puente Alto",
                region = "Metropolitana",
                sitioWeb = "https://municipalidad-ejemplo.cl",
                publicoAsociado = "Funcionarios municipales y familias de la comuna",
                observaciones = "Organización ficticia de ejemplo",
                latitud = -33.6116,
                longitud = -70.5755
            )
        )
        val idInnovaTech = baseDeDatos.organizacionDao().insertar(
            Organizacion(
                nombre = "InnovaTech SpA",
                tipo = TipoOrganizacion.EMPRESA,
                rubro = "Tecnología",
                comuna = "Providencia",
                region = "Metropolitana",
                sitioWeb = "https://innovatech-ejemplo.cl",
                publicoAsociado = "Trabajadores y familias",
                observaciones = "Organización ficticia de ejemplo",
                latitud = -33.4314,
                longitud = -70.6093
            )
        )
        val idPlaza = baseDeDatos.organizacionDao().insertar(
            Organizacion(
                nombre = "Centro Comercial Plaza Ficticia",
                tipo = TipoOrganizacion.CENTRO_COMERCIAL,
                rubro = "Retail",
                comuna = "La Florida",
                region = "Metropolitana",
                sitioWeb = "https://plaza-ejemplo.cl",
                publicoAsociado = "Visitantes y vecinos de la comuna",
                observaciones = "Organización ficticia de ejemplo",
                latitud = -33.5225,
                longitud = -70.5975
            )
        )

        val idEventoInnovaTech = baseDeDatos.eventoDao().insertar(
            Evento(
                nombre = "Feria de Beneficios InnovaTech 2026",
                tipo = TipoEvento.FERIA_EMPRESARIAL,
                descripcion = "Feria interna de beneficios para trabajadores",
                fechaInicio = ahora + 20 * DIA,
                fechaTermino = ahora + 21 * DIA,
                horario = "09:00 a 18:00",
                direccion = "Av. Ficticia 1234",
                comuna = "Providencia",
                region = "Metropolitana",
                organizacionId = idInnovaTech,
                publicoEsperado = "Trabajadores de la empresa",
                publicoObjetivo = "Trabajadores y familias",
                cantidadAsistentesEstimada = 300,
                condicionesParticipacion = "Stand de 3x3 metros con energía eléctrica",
                requisitos = "Seguro de responsabilidad civil",
                costosReferenciales = "2 UF referenciales",
                espaciosDisponibles = "10 stands",
                observaciones = "Evento ficticio de ejemplo"
            )
        )
        val idEventoMunicipal = baseDeDatos.eventoDao().insertar(
            Evento(
                nombre = "Jornada de Bienestar Municipal",
                tipo = TipoEvento.JORNADA_BENEFICIOS,
                descripcion = "Jornada de beneficios para funcionarios municipales",
                fechaInicio = ahora + 35 * DIA,
                fechaTermino = null,
                horario = "10:00 a 16:00",
                direccion = "Plaza de Armas Ficticia 100",
                comuna = "Puente Alto",
                region = "Metropolitana",
                organizacionId = idMunicipalidad,
                publicoEsperado = "Funcionarios municipales",
                publicoObjetivo = "Funcionarios y sus familias",
                cantidadAsistentesEstimada = 150,
                condicionesParticipacion = "Stand de 2x2 metros",
                requisitos = "Listado de servicios a ofrecer",
                costosReferenciales = "Sin costo",
                espaciosDisponibles = "5 stands",
                observaciones = ""
            )
        )
        val idEventoPlaza = baseDeDatos.eventoDao().insertar(
            Evento(
                nombre = "Expo Familias Plaza Ficticia",
                tipo = TipoEvento.FERIA_INSTITUCIONAL,
                descripcion = "Feria de servicios para visitantes del centro comercial",
                fechaInicio = ahora + 50 * DIA,
                fechaTermino = ahora + 52 * DIA,
                horario = "11:00 a 20:00",
                direccion = "Av. Comercial Ficticia 500",
                comuna = "La Florida",
                region = "Metropolitana",
                organizacionId = idPlaza,
                publicoEsperado = "Visitantes del centro comercial",
                publicoObjetivo = "Familias",
                cantidadAsistentesEstimada = 500,
                condicionesParticipacion = "Módulo de 2x2 metros",
                requisitos = "Propuesta de activación",
                costosReferenciales = "1 UF referencial",
                espaciosDisponibles = "8 módulos",
                observaciones = ""
            )
        )
        val idEventoSalud = baseDeDatos.eventoDao().insertar(
            Evento(
                nombre = "Feria de Salud Preventiva",
                tipo = TipoEvento.FERIA_INSTITUCIONAL,
                descripcion = "Feria de servicios de salud y convenios",
                fechaInicio = ahora - 30 * DIA,
                fechaTermino = ahora - 30 * DIA,
                horario = "09:00 a 14:00",
                direccion = "Av. Ficticia 1234",
                comuna = "Providencia",
                region = "Metropolitana",
                organizacionId = idInnovaTech,
                publicoEsperado = "Trabajadores de la empresa",
                publicoObjetivo = "Trabajadores",
                cantidadAsistentesEstimada = 200,
                condicionesParticipacion = "Stand de 3x3 metros",
                requisitos = "Seguro de responsabilidad civil",
                costosReferenciales = "Sin costo",
                espaciosDisponibles = "6 stands",
                observaciones = "Evento ficticio ya realizado"
            )
        )

        baseDeDatos.contactoDao().insertar(
            Contacto(
                nombre = "Camila Rojas",
                organizacionId = idInnovaTech,
                cargo = "Encargada de Bienestar",
                area = AreaContacto.BIENESTAR,
                correo = "camila.rojas@ejemplo.cl",
                telefono = "+56 9 0000 0001",
                medioPreferente = MedioContacto.CORREO,
                estado = EstadoContacto.ACTIVO,
                fechaUltimoContacto = ahora - 4 * DIA,
                observaciones = "Contacto ficticio de ejemplo"
            )
        )
        baseDeDatos.contactoDao().insertar(
            Contacto(
                nombre = "Diego Fuentes",
                organizacionId = idMunicipalidad,
                cargo = "Encargado de Recursos Humanos",
                area = AreaContacto.RECURSOS_HUMANOS,
                correo = "diego.fuentes@ejemplo.cl",
                telefono = "+56 9 0000 0002",
                medioPreferente = MedioContacto.REUNION,
                estado = EstadoContacto.ACTIVO,
                fechaUltimoContacto = ahora - 10 * DIA,
                observaciones = "Contacto ficticio de ejemplo"
            )
        )
        baseDeDatos.contactoDao().insertar(
            Contacto(
                nombre = "Valentina Soto",
                organizacionId = idPlaza,
                cargo = "Coordinadora de Marketing",
                area = AreaContacto.MARKETING,
                correo = "valentina.soto@ejemplo.cl",
                telefono = "+56 9 0000 0003",
                medioPreferente = MedioContacto.TELEFONO,
                estado = EstadoContacto.ACTIVO,
                fechaUltimoContacto = null,
                observaciones = "Contacto ficticio de ejemplo"
            )
        )

        val idOportunidadFeria = baseDeDatos.oportunidadDao().insertar(
            Oportunidad(
                nombre = "Feria de Beneficios InnovaTech 2026",
                tipo = TipoEvento.FERIA_EMPRESARIAL,
                descripcion = "Participar con stand de convenios dentales y veterinarios",
                fuenteInformacion = "LinkedIn",
                fechaDeteccion = ahora - 5 * DIA,
                responsableId = idAna,
                prioridad = Prioridad.ALTA,
                potencialComercial = "Alto: 300 trabajadores estimados",
                estado = EstadoOportunidad.CONTACTO_REALIZADO,
                eventoId = idEventoInnovaTech,
                fechaActualizacion = ahora - 2 * DIA,
                observaciones = ""
            )
        )
        val idOportunidadMunicipal = baseDeDatos.oportunidadDao().insertar(
            Oportunidad(
                nombre = "Jornada de Bienestar Municipal",
                tipo = TipoEvento.ACTIVIDAD_MUNICIPAL,
                descripcion = "Presentar convenios a funcionarios municipales",
                fuenteInformacion = "Sitio web institucional",
                fechaDeteccion = ahora - 12 * DIA,
                responsableId = idAna,
                prioridad = Prioridad.MEDIA,
                potencialComercial = "Medio: 150 funcionarios estimados",
                estado = EstadoOportunidad.PROPUESTA_ENVIADA,
                eventoId = idEventoMunicipal,
                fechaActualizacion = ahora - 1 * DIA,
                observaciones = ""
            )
        )
        val idOportunidadPlaza = baseDeDatos.oportunidadDao().insertar(
            Oportunidad(
                nombre = "Expo Familias Plaza Ficticia",
                tipo = TipoEvento.ACTIVACION_COMERCIAL,
                descripcion = "Activación comercial en centro comercial",
                fuenteInformacion = "Instagram",
                fechaDeteccion = ahora - 3 * DIA,
                responsableId = idMarcos,
                prioridad = Prioridad.BAJA,
                potencialComercial = "Por evaluar",
                estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
                eventoId = idEventoPlaza,
                fechaActualizacion = ahora - 3 * DIA,
                observaciones = ""
            )
        )
        val idOportunidadSalud = baseDeDatos.oportunidadDao().insertar(
            Oportunidad(
                nombre = "Feria de Salud Preventiva",
                tipo = TipoEvento.FERIA_INSTITUCIONAL,
                descripcion = "Stand de convenios de salud en feria interna",
                fuenteInformacion = "Referencia interna",
                fechaDeteccion = ahora - 60 * DIA,
                responsableId = idAna,
                prioridad = Prioridad.MEDIA,
                potencialComercial = "Medio: 200 trabajadores estimados",
                estado = EstadoOportunidad.ACTIVACION_REALIZADA,
                eventoId = idEventoSalud,
                fechaActualizacion = ahora - 30 * DIA,
                observaciones = ""
            )
        )

        baseDeDatos.seguimientoDao().insertar(
            Seguimiento(
                oportunidadId = idOportunidadFeria,
                usuarioResponsableId = idAna,
                tipoAccion = TipoAccionSeguimiento.PRIMER_CONTACTO,
                fechaHora = ahora - 4 * DIA,
                resultado = "Se contactó a Camila Rojas por correo",
                proximaAccion = "Enviar carpeta de servicios",
                fechaProximoSeguimiento = ahora + 2 * DIA,
                observaciones = ""
            )
        )
        baseDeDatos.seguimientoDao().insertar(
            Seguimiento(
                oportunidadId = idOportunidadFeria,
                usuarioResponsableId = idAna,
                tipoAccion = TipoAccionSeguimiento.ENVIO_PRESENTACION,
                fechaHora = ahora - 2 * DIA,
                resultado = "Presentación enviada con convenios disponibles",
                proximaAccion = "Confirmar recepción del material",
                fechaProximoSeguimiento = ahora + 3 * DIA,
                observaciones = ""
            )
        )
        baseDeDatos.seguimientoDao().insertar(
            Seguimiento(
                oportunidadId = idOportunidadMunicipal,
                usuarioResponsableId = idAna,
                tipoAccion = TipoAccionSeguimiento.ENVIO_PROPUESTA,
                fechaHora = ahora - 1 * DIA,
                resultado = "Propuesta enviada a la municipalidad",
                proximaAccion = "Llamar para revisar condiciones",
                fechaProximoSeguimiento = ahora + 5 * DIA,
                observaciones = ""
            )
        )
        baseDeDatos.seguimientoDao().insertar(
            Seguimiento(
                oportunidadId = idOportunidadPlaza,
                usuarioResponsableId = idMarcos,
                tipoAccion = TipoAccionSeguimiento.BUSQUEDA_CONTACTO,
                fechaHora = ahora - 6 * DIA,
                resultado = "Se identificó a Valentina Soto como coordinadora de marketing",
                proximaAccion = "Llamar para confirmar participación en Expo Familias",
                fechaProximoSeguimiento = ahora - 2 * DIA,
                observaciones = "Seguimiento atrasado de ejemplo"
            )
        )

        baseDeDatos.resultadoActivacionDao().guardar(
            ResultadoActivacion(
                oportunidadId = idOportunidadSalud,
                fechaRealizacion = ahora - 30 * DIA,
                estadoFinal = EstadoOportunidad.ACTIVACION_REALIZADA,
                cantidadAsistentes = 210,
                contactosGenerados = 45,
                personasInteresadas = 60,
                inscripciones = 18,
                observaciones = "Alta afluencia durante la mañana",
                evaluacionGeneral = "Buena recepción de los convenios dentales",
                aprendizajes = "Reforzar material impreso y llegar con anticipación"
            )
        )
    }
}
