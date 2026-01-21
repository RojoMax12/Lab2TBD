# Laboratorio N°2 - Taller de Bases de Datos

Proyecto correspondiente al **Laboratorio 2 de Taller de Bases de Datos (TBD)**. Este repositorio contiene un sistema compuesto por un **Backend en Spring Boot (Java)**, un **Frontend en Vue.js 3** y una **Base de Datos PostgreSQL con extensión PostGIS**, comunicados mediante peticiones HTTP y con autenticación basada en JWT. Se busca la integración conceptos de modelado de datos, consultas espaciales y desarrollo de aplicaciones.

El **Objetivo** es desarrollar una plataforma integral para una empresa de recolección de basura, que permita la gestión logística de recolección de basura utilizando geometrías para representar rutas de camiones y ubicaciones de contenedores.

---

## Integrantes

* Antonia Cisternas
* Fabian Escobedo
* José Muñoz
* Johan Neira
* Nicole Torres

---

## Tecnologías

* **Base de Datos:** PostgreSQL con extensión PostGIS
* **Backend:** Spring Boot con Java
* **Frontend:** Vue.js 3
* **Seguridad:** JSON Web Tokens (JWT) para autenticación y autorización
* **Control de Versiones:** Git y repositorio en GitHub

---
## Clonación del Repositorio

Antes de ejecutar el proyecto, es necesario clonar el repositorio desde GitHub.

Desde una terminal, ejecutar:

```bash
git clone https://github.com/RojoMax12/Lab2TBD.git
```

## Ejecución del Proyecto

### 1. Creación de la Base de Datos

Este proyecto utiliza **PostgreSQL** con la extensión **PostGIS** para el manejo de datos geográficos y relacionales.

Antes de ejecutar el sistema es necesario crear y cargar la base de datos en **PostgreSQL** ejecutando el script **`CrearTablasGeoespaciales.sql` y `CrearTablas.sql`**, así como importar la data desde los archivos CSV siguiendo el instructivo. Desde un sistema con distribución **Windows**:

Primeramente se deben crear las tablas geoespaciales. Se puede realizar desde **Query Tool** o desde la terminal:

```bash
createdb -U postgres TBDC2
psql -U postgres -d TBDC2 -f CrearTablasGeoespaciales.sql
```

Una vez creadas las tablas geoespaciales, debe importar la muestra de datos generados con **QGIS** desde los archivos CSV, los cuales puede descargar accediendo con el correo institucional desde:

```text
https://drive.google.com/drive/u/1/folders/1JdKyBVugpZvneUiPsq6xmMQE4ZeN8P-q
```

Puede realizar la importación mediante el asistente de **pgAdmin** (clic derecho en la tabla > Import/Export Data) o mediante el comando \copy en la terminal de psql. Se recomienda el siguiente orden de carga para respetar las jerarquías territoriales:

```bash
    regiones.csv
    provincias.csv
    comunas.csv
    calles.csv
```

Finalmente, ejecute el script para crear y poblar las tablas de negocio estándar:

```bash
psql -U postgres -d TB TBDC2 -f CrearTablas.sql
```

**Nota:** Se asueme configuración por defecto en **application.properties** del Backend, la cual puede ser ajustada en función de la comodidad.

---

### 2. Configuración del Backend

Para la correcta inicialización del sistema, se debe verificar la conexión del backend con la base de datos. Esta configuración se encuentra definida en el archivo:

```text
Backend-Lab2\src\main\resources\application.properties
```

Configuración por defecto:

```properties
spring.application.name=backend
server.port=8090

db.url=jdbc:postgresql://localhost:5432/TBDlab
db.username=postgres
db.password=1234
```

Estos parámetros pueden ajustarse según la configuración local del entorno.

---

### 3. Ejecución del Backend

El backend se inicializa ejecutando la clase principal:

```text
BackendApplication.java
```

Ubicada en

```text
Backend-Lab2\src\main\java\com\lab\backend\BackendApplication.java
```

O alternativamente desde la terminal:

```bash
mvn clean install
mvn spring-boot:run
```

El backend quedará disponible en:

```text
http://localhost:8090
```

---

### 4. Ejecución del Frontend

Para ejecutar el frontend, es necesario instalar previamente las dependencias del proyecto.

Desde la carpeta del frontend instalamos las dependencias necesarias:

```bash
npm install
npm install leaflet
```

Para iniciar el servidor de desarrollo:

```bash
npm run dev
```

---

### Consideraciones

* PostgreSQL debe estar en ejecución antes de iniciar el Backend.
* Los script SQL deben ejecutarse antes del primer inicio del sistema, lo mismo ocurre para la importación de data desde los CSV.
* El proyecto está configurado para un entorno de desarrollo local.

---

## Uso del Sistema

### Funcionalidades Clave del Sistema

* **Login de Usuario:** Autenticación de conductores y administradores mediante token JWT. El inicio de sesión se encuentra en la esquina superior derecha de la interfaz. Dependiendo del tipo de cuenta, se habilitan los permisos correspondientes. Un administrador puede añadir nuevos administradores desde el menú: `Menú -> Administradores -> Agregar Administrador`.

* **Visualización de Rutas:** Los conductores pueden visualizar en un mapa la ruta asignada del día, incluyendo la ubicación de cada contenedor. Para ello, un administrador debe planificar previamente la ruta desde `Menú -> Rutas -> Planificar Ruta`. Posteriormente, el conductor debe tomar la ruta y visualizarla desde `Menú -> Rutas Asignadas -> Tomar -> Menú -> Inicio`.

* **Gestión de Contenedores:** Permite marcar un contenedor como "vaciado" desde el frontend, lo que actualiza el peso del contenedor en la base de datos. Esta acción puede ser realizada por un administrador editando un contenedor desde `Menú -> Contenedores -> Editar -> Estado = Disponible`.

* **Optimización de Rutas:** La API dispone de un endpoint que, a partir de una lista de contenedores, genera una ruta óptima para su recolección. Este proceso se ejecuta automáticamente cuando un administrador utiliza la opción `Planificar Ruta` (`Menú -> Rutas -> Planificar Ruta`).

* **Monitoreo y Reportes:** Los administradores pueden visualizar resúmenes del desempeño de los conductores, así como el estado de las recolecciones. Estas funcionalidades están disponibles desde `Menú -> Inicio -> Calcular Eficiencia / Obtener Desempeño` y `Menú -> Inicio -> Rutas`.

### Nuevas Consultas SQL Implementadas (Laboratorio N°2)

1. **Generación de Geometría de Ruta:** Durante el procedimiento de creación de rutas, se genera automáticamente el LINESTRING que une los contenedores asignados en orden de visita usando ST_MakeLine. Dicho LINESTRING puede ser visualizado desde la vista de administrado en `Menú -> Rutas -> Rutas Planificadas -> Ver Ruta`. Desde la vista de conductor se ve el respectivo mapa de la ruta asignada en `Menú -> Inicio`.

2. **Cálculo de Kilometraje:** Durante la creación de la ruta, se calcula la distancia total estimada de cada ruta planificada usando ST_Length(trayecto::geography).

3. **Contenedores fuera de Zona:** Se identifican contenedores cuya ubicación (POINT) caiga erróneamente fuera de los polígonos de zonas_recoleccion asignados. Para visualizar las Zonas de Recolección desde la vista de administador se sigue el flujo de `Menú -> Zonas de Recolección -> Zonas Existentes`. Para visualizar aquellos contenedores fuera de las Zonas de Recolección, como administrador: `Menú -> Contenedores fuera de zona`.

4. **Optimización Espacial:** Se encuentra el contenedor más cercano a un punto dado usando el operador de vecindad <-> (K-Nearest Neighbors). Se puede acceder desde la vista de conductor; `Menú -> Inicio -> Buscar contenedor cercano`.

### Consultas SQL Implementadas (Laboratorio N°1)

1. **Cálculo de Eficiencia de Recolección:** Devuelve el tiempo promedio que le toma a cada conductor completar una ruta. Muestra el nombre del conductor y el tiempo promedio en horas para todas las rutas completadas en los últimos 6 meses. Visible para el administrador en `Menú -> Inicio -> Calcular Eficiencia`.

2. **Identificación de Contenedores con Problemas:** Encuentra todos los contenedores que han sido recolectados por más de 3 conductores diferentes en el último año. Muestra el ID del contenedor y la cantidad de conductores únicos que lo han vaciado. Visible para el administrador en `Menú -> Contenedores (Contenedores con problemas)`.

3. **Comparación de Desempeño por Tipo de Residuos:** Compara la cantidad total de residuos recolectados por tipo entre los dos conductores con el mayor número de rutas completadas. Muestra el tipo de residuo, la cantidad recolectada por el conductor A y la cantidad recolectada por el conductor B. Visible para el administrador en `Menú -> Inicio -> Obtener Desempeño`.

4. **Detección de Rutas Ineficientes:** Escribe una consulta que identifique las 5 rutas más largas (en número de contenedores) que, sin embargo, fueron completadas en menos tiempo que el 25% de las rutas más cortas. Muestra el ID de la ruta, el número de contenedores y la duración total. Visible para el administrador en `Menú -> Rutas -> Mostrar Rutas Ineficientes`.

5. **Análisis de Densidad de Contenedores:** Calcula el promedio de contenedores por ruta para cada mes en los últimos 12 meses. Muestra el mes y el promedio de contenedores. Además, incluye una columna que muestre la diferencia del promedio del mes actual con respecto al mes anterior. Visible para el administrador en `Menú -> Contenedores (Análisis de Densidad)`.

6. **Simulación de Planificación de Rutas:** Procedimiento almacenado llamado planificar_ruta que recibe un arreglo de IDs de contenedores. El procedimiento valida que todos los contenedores existan y no estén asignados a una ruta pendiente. Si la validación es exitosa, debe crear una nueva ruta con estado 'Pendiente' y asociar a todos los contenedores proporcionados. Visible para el administrador en `Menú -> Rutas -> Planificar Ruta`.

7. **Actualización Masiva de Contenedores:** Dado un ID de ruta, actualiza el peso (basura) estimado de todos los contenedores de esa ruta a un nuevo valor. Si el nuevo valor es negativo, el procedimiento debe lanzar un error y revertir la transacción. Visible para el administrador en `Menú -> Contenedores -> Actualización masiva de pesos`.

8. **Listado de Contenedores sin Recolección Reciente:** Muestra todos los contenedores que no han sido recolectados en los últimos 90 días. La consulta incluye el ID del contenedor, su tipo de residuo, su ubicación y la fecha de su última recolección. Si nunca ha sido recolectado, debe indicarlo. Visible para el administrador en `Menú -> Contenedores (Contenedores sin recolección reciente)`.

9. **Impacto de la Recolección en el Peso Total:** Vista materializada llamada peso_recolectado_diario que muestra la suma total del peso de los residuos recolectados por día. Esta vista debe ser refrescada concurrentemente y debe poder ser consultada rápidamente por la aplicación para generar gráficos de reporte. Visible para el administrador en `Menú -> Inicio (Peso recolectado diario)`.