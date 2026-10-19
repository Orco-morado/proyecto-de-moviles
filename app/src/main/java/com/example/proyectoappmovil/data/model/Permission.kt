package com.example.proyectoappmovil.data.model

enum class Permission {
    // Oportunidades
    VER_OPORTUNIDADES_PROPIAS,
    VER_TODAS_OPORTUNIDADES,
    CREAR_OPORTUNIDAD,
    EDITAR_OPORTUNIDAD_AJENA,
    
    // Seguimientos
    VER_SEGUIMIENTOS,
    CREAR_SEGUIMIENTO,
    
    // Organizaciones y Contactos
    VER_ORGANIZACIONES_CONTACTOS,
    CREAR_EDITAR_ORGANIZACIONES_CONTACTOS,
    
    // Alertas
    VER_ALERTAS,
    
    // Administración
    GESTIONAR_USUARIOS,
    VER_REPORTES_GLOBALES,
    CONFIGURAR_APP
}