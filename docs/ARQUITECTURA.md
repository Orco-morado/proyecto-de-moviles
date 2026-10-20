# Arquitectura del Proyecto

El proyecto está estructurado siguiendo el patrón de diseño **MVVM (Model-View-ViewModel)** recomendado por Google para el desarrollo moderno en Android. Esta arquitectura garantiza una clara separación de responsabilidades, facilitando la escalabilidad, el testing y el mantenimiento del código.

## Diagrama de Flujo de Datos

A continuación, se detalla el flujo unidireccional de los datos desde la interfaz de usuario hasta la persistencia local:

```mermaid
graph TD
    subgraph Capa de Presentacion [Capa de Presentación / UI]
        UI[Pantallas Jetpack Compose]
        VM[ViewModels]
        UI -- "Acciones / Eventos" --> VM
        VM -- "Expone estado (StateFlow)" --> UI
    end

    subgraph Capa de Datos [Capa de Datos y Dominio]
        Repo[Repositorios]
        VM -- "Solicita / Envía datos" --> Repo
    end

    subgraph Capa de Persistencia [Almacenamiento Local]
        DAO[Data Access Objects - DAO]
        Room[(Base de Datos Room)]
        Repo -- "Ejecuta consultas" --> DAO
        DAO -- "Lee / Escribe" --> Room
    end

    subgraph Recursos Nativos [Hardware del Dispositivo]
        GPS[Ubicación / GPS]
        Cam[Cámara Nativa]
        UI -. "Pide permisos e interactúa" .-> GPS
        UI -. "Pide permisos e interactúa" .-> Cam
    end

    %% Estilos para GitHub
    style UI fill:#03DAC5,stroke:#018786,color:#000
    style VM fill:#BB86FC,stroke:#6200EE,color:#000
    style Repo fill:#FFB84D,stroke:#F57C00,color:#000
    style Room fill:#CFD8DC,stroke:#607D8B,color:#000