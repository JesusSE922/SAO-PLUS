# SAO+ --- Informe de estado del diseño del juego

**Proyecto:** Servidor Minecraft SAO+\
**Fecha:** 8 de noviembre de 2026\
**Fase:** Diseño conceptual y definición de sistemas\
**Estado:** Fundamentos establecidos; diseño detallado en desarrollo.

------------------------------------------------------------------------

## 1. Aclaración fundamental sobre el alcance del proyecto

SAO+ no debe entenderse como el desarrollo desde cero de un videojuego
completo ni como la obligación de programar personalmente todos sus
sistemas.

**El objetivo es construir la experiencia de juego del modpack.** Para
conseguirlo, habrá que identificar qué funcionalidades se pueden obtener
de modificaciones existentes, cuáles necesitan desarrollo propio y
cuáles son complementos opcionales.

Esta clasificación será una parte esencial del diseño y, más adelante,
de la planificación técnica:

1.  **Desarrollo propio (mod SAO+):** sistemas o comportamientos
    específicos de la experiencia SAO+ que no puedan cubrirse
    adecuadamente con dependencias existentes. Se implementarán en
    nuestro propio mod cuando sea necesario.
2.  **Dependencias del modpack:** mods existentes que aporten sistemas
    que podamos integrar y utilizar como parte de la experiencia. No
    tendría sentido recrear desde cero una funcionalidad que una
    dependencia resuelva de forma adecuada.
3.  **Complementos opcionales:** mods que mejoren la presentación,
    comodidad, inmersión o variedad, pero cuya ausencia no impida
    completar la experiencia principal.

La clasificación concreta todavía no está decidida. No se debe asumir
que cada sistema descrito en este documento tendrá que programarse desde
cero.

La prioridad es definir **qué debe experimentar el jugador y qué reglas
debe cumplir el juego**. La elección entre desarrollo propio,
dependencia u opción complementaria se realizará posteriormente, cuando
estudiemos las capacidades, limitaciones y compatibilidad de las
herramientas disponibles.

------------------------------------------------------------------------

## 2. Resumen ejecutivo

SAO+ se plantea como un modpack de Minecraft con experiencia MMORPG,
inspirado principalmente en *Sword Art Online*, especialmente en el arco
de Aincrad.

El objetivo es trasladar al modpack la experiencia de un MMORPG de
fantasía con progresión, combate técnico, exploración estructurada,
armas especializadas, jefes y cooperación, utilizando Minecraft como
plataforma.

La dirección elegida es híbrida:

-   Mantener la identidad y los elementos reconocibles de SAO.
-   Incorporar profundidad propia de los MMORPG modernos.
-   Permitir libertad de configuración y desarrollo del personaje.
-   Evitar que la experiencia se convierta en un servidor de
    supervivencia convencional.
-   Hacer que el avance por Aincrad dependa de superar objetivos y
    desafíos relevantes, especialmente los jefes de planta.

El jugador construirá su propio personaje dentro del mundo de SAO; no
controlará a Kirito, Asuna ni a otros personajes existentes.

El proyecto ya cuenta con una dirección de diseño reconocible y varios
sistemas interrelacionados. Todavía no existe una especificación
definitiva de todos ellos.

## 3. Visión general y pilares del juego

### 3.1. Identidad

El modpack combinará tres grandes dimensiones:

1.  **Experiencia SAO:** Aincrad, ciudades, mazmorras, jefes, Sword
    Skills, progresión por plantas y elementos narrativos del anime.
2.  **Experiencia MMORPG:** niveles, atributos, dominio de armas,
    especializaciones, equipo, economía y misiones.
3.  **Experiencia Minecraft:** exploración, recursos, construcción y,
    posteriormente, un mundo exterior con posibilidades adicionales.

### 3.2. Pilares de diseño

**Pilar 1 --- Desarrollo del personaje**

Cada jugador debe poder construir un personaje diferente mediante
atributos, armas, habilidades, especializaciones y equipamiento.

**Pilar 2 --- Combate técnico**

El combate debe premiar la habilidad del jugador. El momento de
defenderse, elegir una respuesta y aprovechar una oportunidad debe
importar tanto como el nivel o el equipo.

**Pilar 3 --- Exploración con propósito**

El mundo debe contener ubicaciones, materiales, enemigos, servicios y
recompensas relevantes. Explorar tiene que aportar algo al progreso.

**Pilar 4 --- Progresión estructurada**

El avance por Aincrad debe tener objetivos claros. Los jugadores no
deberían poder saltarse los principales desafíos mediante construcción,
automatización o acumulación de recursos.

**Pilar 5 --- Libertad con consecuencias**

El jugador puede escoger su estilo de juego, pero las especializaciones
deben tener ventajas y desventajas reales.

------------------------------------------------------------------------

## 4. Mundo y estructura de Aincrad

### 4.1. Estructura por plantas

**Decidido:**

-   Aincrad será el núcleo inicial de la experiencia.
-   Las plantas se desbloquearán progresivamente.
-   No es obligatorio implementar todas las plantas desde el
    lanzamiento.
-   Cada planta tendrá identidad y características propias.
-   Las plantas estarán conectadas físicamente y mediante sistemas de
    viaje.
-   Los jugadores podrán regresar a plantas anteriores.

El desbloqueo de una planta será híbrido. No dependerá exclusivamente de
matar al jefe: también habrá exploración, objetivos, laberintos y
preparación.

### 4.2. Contenido de las plantas anteriores

Se ha elegido un modelo de acceso limitado y adaptado.

Las plantas anteriores no desaparecerán, pero sus recompensas y
actividades podrán ajustarse a la progresión del jugador.

Esto permite mantener la utilidad de las zonas anteriores sin que se
conviertan en lugares de farmeo descontrolado.

### 4.3. Localizaciones importantes

Se han identificado como elementos fundamentales:

-   Pueblo de los Comienzos.
-   Ciudades y asentamientos.
-   Tiendas y herrerías.
-   Mazmorras y cuevas.
-   Laberintos de las plantas.
-   Salas de jefes.
-   Zonas especiales de obtención de materiales.

Las ubicaciones no serán solamente decorativas. Algunas armas, servicios
y materiales deberán estar vinculados a lugares concretos.

### 4.4. Relación con la historia del anime

La historia será paralela a la del anime.

El jugador vivirá su propia aventura dentro de un mundo reconocible, con
acontecimientos y misiones relacionados con SAO, sin convertirse en uno
de sus protagonistas originales.

Todavía no está definida la cronología exacta, la participación de
personajes del anime ni la estructura narrativa de cada planta.

------------------------------------------------------------------------

## 5. Mundo exterior a Aincrad

Una de las decisiones más relevantes del diseño es que el mundo
Minecraft convencional permanecerá inaccesible hasta completar SAO o
alcanzar el momento narrativo equivalente al logout.

**Objetivos del mundo exterior:**

-   Obtener recursos propios de Minecraft.
-   Conseguir materiales y objetos comerciables.
-   Permitir la construcción de viviendas.
-   Incorporar instalaciones propias, como una forja.
-   Ofrecer nuevas actividades después de completar Aincrad.
-   Crear una economía complementaria basada en recursos.

Se ha señalado expresamente que las plantas, granjas y automatizaciones
podrían desequilibrar la economía. Por eso, estos sistemas no deben
estar disponibles durante la progresión principal de Aincrad.

Falta diseñar cómo se desbloquea este mundo, cómo se conecta con el
personaje, qué recursos se pueden trasladar y qué límites tendrá el
comercio.

------------------------------------------------------------------------

## 6. Sistema de personaje y progresión

### 6.1. Filosofía de progresión

El personaje tendrá una progresión híbrida:

-   Nivel general.
-   Atributos.
-   Dominio de cada familia de armas.
-   Habilidades.
-   Especializaciones.
-   Equipamiento.

No existirán clases fijas. Los roles surgirán de las decisiones del
jugador.

Por tanto, dos personajes que utilicen la misma arma podrán desarrollar
estilos de combate diferentes.

### 6.2. Atributos

Se han identificado como candidatos:

-   Fuerza.
-   Vitalidad.
-   Agilidad.
-   Resistencia.
-   Inteligencia.
-   Suerte.

La lista definitiva todavía no está cerrada.

**Decisiones confirmadas:**

-   El jugador distribuirá puntos al subir de nivel.
-   Se utilizará un sistema de softcap: invertir muchos puntos en un
    atributo producirá rendimientos progresivamente menores.
-   La agilidad no debe limitarse a aumentar la velocidad de
    desplazamiento.
-   Podrá influir en aspectos del combate como ejecución de habilidades,
    ventanas de acción y recuperación.

Todavía no se han definido fórmulas, límites ni efectos exactos.

### 6.3. Especialización

Los jugadores podrán desarrollar un estilo de combate sin seleccionar
una clase tradicional.

El cambio de especialización será limitado y tendrá consecuencias, pero
no destruirá el progreso anterior.

**Decisión confirmada sobre el cambio de rama:**

-   Se conservan las habilidades generales del arma.
-   La rama anterior queda desactivada.
-   Su progreso se conserva.
-   Al volver a ella, se recuperan sus habilidades y progresión.
-   Solo se mantiene activa una especialización a la vez dentro de esa
    familia de armas.

------------------------------------------------------------------------

## 7. Sistema de armas

### 7.1. Familias contempladas

Se han identificado nueve familias:

1.  Espada de una mano.
2.  Espada de dos manos.
3.  Doble empuñadura.
4.  Lanzas.
5.  Dagas.
6.  Hachas.
7.  Espada y escudo.
8.  Martillos y mazas.
9.  Arcos.

La lista ampliada está planteada como catálogo de diseño. La
disponibilidad inicial de cada familia y la aprobación de todas sus
particularidades todavía deben cerrarse.

### 7.2. Dominio de armas

La maestría combinará:

-   Experiencia obtenida mediante el uso.
-   Desafíos específicos.
-   Desbloqueos de técnicas.
-   Progresión propia de cada familia.

El dominio de un arma no debería perderse simplemente por cambiar
temporalmente a otra.

### 7.3. Doble empuñadura

La doble empuñadura tendrá un tratamiento excepcional.

No será una clase ni una opción inicial para todos. Se desbloqueará
mediante esfuerzo especial, requisitos de progresión y desafíos que
demuestren dominio del combate.

Su funcionamiento deberá ser propio, no una simple duplicación del daño
por equipar dos armas.

### 7.4. Equipamiento y armas especiales

El equipo podrá modificar significativamente el estilo de combate.

Sin embargo, las habilidades fundamentales de una familia no dependerán
de conseguir un arma extremadamente rara.

Las armas legendarias tendrán dos funciones:

-   Incorporar habilidades exclusivas.
-   Modificar o potenciar habilidades ya existentes.

El objetivo es que sean especiales sin convertir el equipo normal en
algo inútil.

------------------------------------------------------------------------

## 8. Especializaciones y ramas

Se ha definido una filosofía importante: cada especialización debe
implicar una renuncia.

Una rama no puede limitarse a mejorar determinadas estadísticas. También
debe empeorar otras acciones o introducir limitaciones mecánicas.

Ejemplos conceptuales:

-   Una rama defensiva mejora el bloqueo, pero reduce la presión
    ofensiva.
-   Una rama ofensiva aumenta el daño, pero empeora la capacidad
    defensiva.
-   Una rama de duelista mejora el parry, pero puede reducir la
    resistencia ante golpes directos.

Estas son orientaciones, no ramas aprobadas definitivamente.

### Estructura propuesta

Cada familia tendría:

-   Habilidades generales.
-   Varias ramas de especialización.
-   Técnicas propias de cada rama.
-   Habilidades avanzadas.
-   Una técnica culminante.

El número de ramas dependerá de las posibilidades de cada arma.

Todavía falta aprobar el número definitivo de ramas y su contenido.

------------------------------------------------------------------------

## 9. Sistema de combate

El combate es uno de los sistemas más desarrollados conceptualmente.

### 9.1. Filosofía

El combate debe estar inspirado en SAO:

-   Técnicas espectaculares.
-   Importancia de la ejecución.
-   Diferencias entre armas.
-   Defensas activas.
-   Ventanas de oportunidad.
-   Enemigos con patrones reconocibles.
-   Cooperación en encuentros difíciles.

El combate no debe resolverse simplemente comparando niveles y
estadísticas.

Un jugador de nivel inferior debería poder superar a un enemigo más
fuerte si juega mejor, aunque el nivel y el equipamiento sigan siendo
relevantes.

### 9.2. Ataques enemigos

Los ataques normales se comunicarán principalmente mediante animaciones.

Los ataques importantes, especialmente los golpes pesados, podrán
incorporar indicadores visuales.

Se busca claridad sin depender de cálculos físicos costosos ni de
sistemas de detección excesivamente complejos.

### 9.3. Defensas universales

**Confirmado: todos los jugadores podrán bloquear, esquivar y hacer
parry.**

La especialización determinará la eficacia y las posibilidades de cada
defensa.

Las tres acciones tendrán funciones diferenciadas:

-   **Bloqueo:** reducir o detener el daño, según arma y
    especialización.
-   **Esquiva:** abandonar el área de impacto o utilizar una ventana de
    invulnerabilidad.
-   **Parry:** responder al ataque enemigo y generar una oportunidad
    ofensiva.

### 9.4. Parry y contraataque

El parry será una mecánica central.

Un parry satisfactorio podrá abrir una ventana para ejecutar un ataque o
una Sword Skill compatible.

La recompensa dependerá del enemigo y del tipo de ataque.

No todos los parrys producirán aturdimiento. Es necesario distinguir
entre:

-   Interrupción del ataque enemigo.
-   Aturdimiento.
-   Vulnerabilidad o apertura para contraatacar.

Los enemigos grandes y los jefes podrán resistir el aturdimiento sin ser
inmunes a todas las consecuencias defensivas.

### 9.5. Sword Skills

Las Sword Skills serán una parte fundamental del combate.

No se busca una barra convencional de habilidades con ataques activados
sin contexto.

La activación deberá considerar requisitos, estados del combate y
posibles combinaciones de botones.

Se ha elegido utilizar MP y tiempos de reutilización como recursos de
gestión.

Todavía falta cerrar cómo se recupera el MP y qué habilidades pueden
encadenarse.

### 9.6. Jefes

Los jefes combinarán estadísticas y mecánicas.

La intención es que requieran preparación, conocimiento de sus ataques y
cooperación, sin excluir por completo la posibilidad de jugar en
solitario.

Todavía no está definida la estructura exacta de los encuentros ni el
grado de dificultad para grupos pequeños.

------------------------------------------------------------------------

## 10. Misiones y contenido

Se han identificado cinco categorías:

-   Misiones principales.
-   Misiones secundarias.
-   Misiones de exploración.
-   Misiones de jefes.
-   Misiones ocultas.

No se ha decidido incorporar misiones diarias ni misiones de gremio.

Las misiones estarán vinculadas al mundo y a la historia, evitando que
la progresión se reduzca a repetir actividades sin contexto.

Falta diseñar la estructura narrativa, los requisitos, las recompensas y
la relación entre misiones y desbloqueo de plantas.

------------------------------------------------------------------------

## 11. Economía y recursos

La moneda principal será el COL.

Se han identificado como elementos fundamentales:

-   Tiendas.
-   Herrerías.
-   Compra y venta de armas.
-   Materiales de mejora.
-   Recursos comunes.
-   Recursos especiales asociados a regiones.
-   Armas únicas y legendarias.

La economía debe estar conectada con la exploración y la progresión.

La obtención de recursos especiales deberá incentivar visitar lugares
concretos.

El mundo exterior añadirá otra fuente de recursos y comercio, pero no
debe permitir saltarse la progresión principal.

Todavía no se han definido precios, fuentes de COL, límites comerciales,
sistemas de intercambio ni mecanismos contra la inflación.

------------------------------------------------------------------------

## 12. Supervivencia, muerte y cooperación

### Supervivencia

Se ha elegido una supervivencia ligera.

La comida, el descanso y los consumibles serán útiles, pero no deben
convertir el modpack en una experiencia centrada en hambre, sed y
gestión constante de necesidades.

### Muerte

Las consecuencias serán contextuales.

No todas las muertes tendrán necesariamente el mismo resultado. Falta
definir qué sucede al morir en una ciudad, una mazmorra, una planta
avanzada o un enfrentamiento contra un jefe.

### Cooperación

El modpack estará orientado a un grupo privado de amigos.

Se desea que sea posible jugar en solitario, pero todavía no está
determinado si todos los contenidos principales deberán poder
completarse sin otros jugadores.

------------------------------------------------------------------------

## 13. Interfaz y presentación

Se han establecido algunos requisitos visuales:

-   Menú de personaje con apariencia holográfica.
-   Transiciones visuales al abrir interfaces.
-   Información clara de estadísticas, armas y habilidades.
-   Indicadores de ataques importantes.
-   Presentación espectacular de Sword Skills.

También se quiere mantener compatibilidad con dos tipos de
modificaciones visuales:

-   Mapas del mundo en 3D.
-   Sistemas de cámara que cambian el encuadre o alejan la cámara al
    abrir interfaces.

Estos elementos son complementarios: no deben ser requisitos para que la
jugabilidad funcione.

------------------------------------------------------------------------

## 14. Decisiones que todavía faltan

  Sistema             Trabajo pendiente
  ------------------- --------------------------------------------
  Progresión          Niveles, experiencia, curvas y requisitos.
  Atributos           Lista definitiva y efectos.
  Armas               Catálogo definitivo y disponibilidad.
  Maestría            Rangos, desafíos y recompensas.
  Especializaciones   Ramas y habilidades concretas.
  Doble empuñadura    Requisitos y funcionamiento completo.
  Parry               Ventanas, tolerancias y resultados.
  Bloqueo y esquiva   Reglas y diferencias por arma.
  Sword Skills        Activación, cadenas y compatibilidades.
  MP                  Costes y recuperación.
  Equipamiento        Rarezas, estadísticas y mejoras.
  Enemigos            Tipos, resistencias y comportamientos.
  Jefes               Mecánicas, fases y escalado.
  Economía            Fuentes de COL, precios y comercio.
  Mundo               Diseño de plantas y localizaciones.
  Misiones            Estructura y recompensas.
  Muerte              Consecuencias por contexto.
  Cooperación         Escalado y viabilidad en solitario.
  Mundo exterior      Desbloqueo, economía y construcción.
  Construcción        Límites y relación con la progresión.
  Interfaz            Menús, HUD y presentación.

------------------------------------------------------------------------

## 15. Riesgos de diseño identificados

### 15.1. Exceso de complejidad

Nueve familias de armas, varias ramas por arma, atributos, habilidades y
equipamiento pueden generar un volumen elevado de contenido.

Será necesario reutilizar estructuras y sistemas comunes sin que las
armas pierdan identidad.

### 15.2. Desequilibrio entre atributos

La agilidad puede resultar excesivamente poderosa si reduce demasiados
tiempos de recuperación o amplía las ventanas de defensa.

El softcap ayudará, pero no sustituye al equilibrio entre estadísticas.

### 15.3. Parry obligatorio

Si el parry resulta demasiado superior al bloqueo o a la esquiva, todas
las configuraciones terminarán jugando igual.

Las tres defensas deben tener situaciones donde sean útiles.

### 15.4. Economía del mundo exterior

La automatización y la producción ilimitada podrían destruir el valor de
los materiales y del COL.

El acceso progresivo al mundo exterior debe acompañarse de límites
económicos.

### 15.5. Progresión de los jefes

El diseño debe evitar que el nivel y el equipo conviertan a los jefes en
simples comprobaciones de estadísticas.

Las mecánicas y la ejecución deben conservar importancia.

### 15.6. Alcance de desarrollo

Intentar implementar desde cero todos los sistemas descritos sería una
tarea desproporcionada y no es un requisito del proyecto.

La solución debe surgir de combinar, cuando sea viable, desarrollo
propio, dependencias existentes y complementos opcionales. La decisión
debe basarse en el resultado que necesita el jugador, la calidad de las
soluciones disponibles, su compatibilidad y el coste de mantenerlas.

------------------------------------------------------------------------

## 16. Clasificación futura de sistemas por origen

Esta clasificación será una fase de trabajo propia. Todavía no se asigna
ningún sistema concreto a una categoría.

  -----------------------------------------------------------------------
  Categoría                           Criterio
  ----------------------------------- -----------------------------------
  **Desarrollo propio --- mod SAO+**  Funcionalidad específica que no
                                      esté cubierta de forma
                                      satisfactoria por las dependencias
                                      disponibles o que necesite
                                      integración y reglas propias.

  **Dependencia del modpack**         Mod existente que aporta una
                                      funcionalidad adecuada y que se
                                      incorpora como parte necesaria de
                                      la experiencia diseñada.

  **Opcional**                        Complemento que mejora
                                      presentación, inmersión, comodidad
                                      o variedad, pero que no es
                                      imprescindible para completar el
                                      juego.
  -----------------------------------------------------------------------

La evaluación futura de cada sistema deberá considerar:

-   Si existe una solución ya disponible.
-   Si se ajusta a la experiencia diseñada.
-   Si permite configuración e integración suficientes.
-   Si es compatible con el resto del modpack.
-   Su rendimiento y estabilidad.
-   El mantenimiento y la dependencia de terceros.
-   Qué ocurre si se elimina o deja de actualizarse.

**Principio rector:** diseñar primero la experiencia deseada y decidir
después qué herramienta la proporciona. No adaptar innecesariamente la
visión del juego a las limitaciones de una tecnología elegida demasiado
pronto.

------------------------------------------------------------------------

## 17. Evaluación del estado del proyecto

  -----------------------------------------------------------------------
  Área                                Estado
  ----------------------------------- -----------------------------------
  Visión general                      Fundamentos definidos

  Identidad SAO                       Fundamentos definidos

  Estructura de Aincrad               Parcialmente definida

  Mundo exterior                      Concepto definido

  Personaje y progresión              Parcialmente definido

  Sistema de armas                    Catálogo propuesto

  Especializaciones                   Estructura propuesta

  Combate                             Fundamentos bastante desarrollados

  Sword Skills                        Parcialmente definido

  Jefes                               Concepto general definido

  Misiones                            Tipos definidos

  Economía                            Fundamentos definidos

  Supervivencia y muerte              Filosofía definida

  Cooperación                         Parcialmente definida

  Interfaz                            Requisitos generales definidos

  Reparto entre desarrollo propio y   Pendiente de una fase específica
  dependencias                        
  -----------------------------------------------------------------------

### Valoración

La mayor parte del trabajo realizado hasta ahora ha consistido en
establecer las reglas que deben definir la experiencia.

El combate y la progresión del personaje son los apartados con mayor
desarrollo conceptual. La economía, el diseño de plantas, las misiones y
la estructura de contenido necesitan todavía bastante trabajo.

No se ha seleccionado todavía el modpack definitivo ni se han tomado
decisiones de implementación. Esto es deliberado: primero debe quedar
claro qué experiencia queremos construir.

------------------------------------------------------------------------

## 18. Próximas fases recomendadas

**Fase 1 --- Cerrar la progresión del personaje** - Definir atributos. -
Establecer niveles y experiencia. - Definir rangos de maestría. - Cerrar
las reglas de especialización.

**Fase 2 --- Diseñar el combate** - Definir bloqueo, esquiva y parry. -
Establecer Sword Skills. - Diseñar el sistema de MP. - Definir enemigos
y resistencias. - Establecer las reglas de los jefes.

**Fase 3 --- Diseñar Aincrad** - Definir estructura de plantas. -
Identificar localizaciones. - Diseñar mazmorras y laberintos. - Definir
desbloqueos y progresión narrativa.

**Fase 4 --- Diseñar economía y contenido** - COL. - Tiendas y
herrerías. - Materiales y mejoras. - Misiones y recompensas. - Armas
especiales.

**Fase 5 --- Diseñar el mundo exterior** - Desbloqueo. - Recursos. -
Construcción. - Forjas. - Comercio y equilibrio económico.

**Fase 6 --- Consolidar el GDD** - Unificar todas las decisiones. -
Eliminar contradicciones. - Definir requisitos funcionales desde la
perspectiva del jugador. - Identificar sistemas imprescindibles y
opcionales.

**Fase 7 --- Estudio de soluciones técnicas** - Revisar mods
existentes. - Evaluar compatibilidad y limitaciones. - Clasificar cada
funcionalidad como desarrollo propio, dependencia u opcional. -
Delimitar el alcance real del mod SAO+. - Planificar la implementación
solo después de esa evaluación.

------------------------------------------------------------------------

## 19. Conclusión

SAO+ ya tiene una identidad clara: un modpack con experiencia MMORPG
inspirado en Aincrad, progresión estructurada, libertad para construir
personajes, combate técnico y una economía vinculada a la exploración.

La decisión más importante es que la experiencia no debe depender
exclusivamente de estadísticas ni de la libertad sandbox de Minecraft.
El jugador debe avanzar porque aprende, mejora su personaje, explora y
supera desafíos.

También queda establecido que **el objetivo no es programar todo desde
cero**. El objetivo es conseguir la experiencia diseñada utilizando la
combinación más razonable de desarrollo propio, dependencias y
complementos opcionales.

El siguiente objetivo es transformar las ideas actuales en sistemas
completos, con reglas concretas y relaciones claras entre ellos. Una vez
definidos, se podrá estudiar qué herramientas permiten construir cada
parte de la experiencia con el menor coste técnico y de mantenimiento
posible.
