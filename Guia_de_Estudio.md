# Guía de Estudio Rápida - Diamond Casino

Esta guía contiene las posibles preguntas y respuestas clave para la presentación final del proyecto.

## FASE 1: LOS CIMIENTOS (Modelos)

### 1. Clases de Usuarios (`Usuario.java`, `Admin.java`, `Empleado.java`, `Cliente.java`)

**P: Veo que la clase Usuario dice `public abstract class Usuario`. ¿Por qué es abstracta y qué significa eso?**
**R:** Es abstracta porque funciona como una 'plantilla'. En la vida real no existe un 'usuario genérico', o eres Admin, o eres Empleado o eres Cliente. Al hacerla abstracta, obligamos a que nadie pueda crear un objeto Usuario vacío por accidente; a fuerzas tienen que instanciar a sus hijos.

**P: ¿Por qué pusiste las variables como `protected` (ej. `protected String idUsuario;`) en vez de `private` como manda el encapsulamiento?**
**R:** Porque al aplicar **Herencia**, los hijos (`Admin`, `Empleado`, `Cliente`) necesitan tener acceso directo a esos atributos de su padre. Si los pongo `private`, se bloquean y los hijos no los pueden heredar bien.

**P: Muéstrame dónde aplicaste Polimorfismo en tu proyecto.**
**R:** En la clase `Usuario` hay un método vacío que dice `public abstract String obtenerNivelAcceso();`. Cada uno de sus hijos (`Admin`, `Empleado`, `Cliente`) usa la etiqueta `@Override` para sobreescribir ese método y devolver un mensaje distinto. Así, el sistema le puede preguntar a cualquier usuario su nivel de acceso, y cada uno responderá de forma diferente según su forma (polimorfismo).

---

### 2. Clase de Productos (`Producto.java`)

**P: ¿Por qué dice `implements Serializable` allá arriba, y para qué sirve ese `serialVersionUID = 1L`?**
**R:** Usamos `Serializable` para poder guardar el inventario completo como un archivo binario (`.dat`) en el disco duro. En lugar de estar guardando manualmente campo por campo en un archivo de texto, Java 'congela' el objeto entero a bytes y lo guarda de golpe. 
El `serialVersionUID = 1L` es un sello de versión; sirve para que, si en el futuro le agregamos otro atributo a la clase, el programa no colapse al intentar leer archivos viejos.

**P: Aquí veo puro `private`. ¿Cuál es la diferencia contra el `protected` que usamos en los usuarios?**
**R:** Esto es **Encapsulamiento** puro. Como la clase Producto no tiene hijos (no usamos herencia aquí), bloqueamos los atributos con `private` para que NADIE desde afuera pueda modificarlos directamente. Para interactuar con ellos tienen que usar obligatoriamente las "puertas" controladas: los métodos `getters` y `setters`.

**P: ¿Y qué hace ese `@Override public String toString()` hasta abajo?**
**R:** Normalmente, si imprimimos un objeto en Java, imprime su dirección de memoria en la RAM (ej. `Producto@7a81197d`). Al sobreescribir (`@Override`) el método `toString()`, obligamos a Java a ignorar ese comportamiento por defecto y que nos devuelva una cadena de texto ordenada con el nombre, precio y stock, lista para mostrarse directamente en los cuadros de texto de la Interfaz Gráfica.


### 3. Las Cuentas y las Ventas (CuentaCliente.java, Venta.java, DetalleVenta.java)

**P: ¿Cómo están conectadas estas tres clases entre sí?**
**R:** Están conectadas 'en cascada' (Estructuras Relacionales) usando ArrayList. 
1. Un CuentaCliente tiene una lista de Venta.
2. Una Venta tiene una lista de DetalleVenta.
3. Un DetalleVenta guarda 1 objeto Producto y cuántas piezas se vendieron.
Es como una caja dentro de otra caja.

**P: Veo que en Venta.java hay un método agregarDetalle(). ¿Por qué se calcula el total ahí y no en otro lado?**
**R:** Porque queremos un *Cálculo Dinámico*. En lugar de tener el total estático y correr el riesgo de que se desactualice si agregamos o quitamos productos de la cuenta, cada vez que agregamos un DetalleVenta, el total de la venta se auto-suma. Así aseguramos que la información financiera siempre sea correcta.

**P: ¿De dónde sale el ID de las ventas nuevas en Venta.java?**
**R:** En el constructor usamos System.currentTimeMillis(). Como esto da los milisegundos exactos desde 1970, es matemáticamente imposible que dos ventas se registren en el mismo milisegundo exacto, dándonos un Ticket ID 100% único automáticamente sin usar bases de datos complejas.


### PREGUNTAS CLAVE ADICIONALES SOBRE MODELOS

**P: ¿Para qué sirve y qué retorna getHistorialVentas() en CuentaCliente?**
**R:** Retorna toda la caja entera, es decir, el objeto ArrayList<Venta> crudo. Lo usamos en PanelHistorial.java para hacer un ciclo for y pintar ticket por ticket en la pantalla del cliente. También lo usa ReportesManager para cruzar datos financieros.

**P: En Venta.java, ¿por qué hay un constructor que genera el ID con System.currentTimeMillis() y tiene el comentario "para que no explote la GUI"?**
**R:** Porque hay dos constructores: el normal lo usamos cuando leemos ventas viejas de ventas.txt (que ya traen ID). El de milisegundos lo usa la interfaz gráfica al hacer una venta nueva. Si obligáramos a la GUI o al empleado a inventar un ID único manualmente cada vez, el programa colapsaría si ingresan uno repetido. Usar milisegundos automatiza esto y nos da un ID 100% único sin usar bases de datos complejas.

**P: ¿Qué retorna getDetalles() en Venta.java?**
**R:** Retorna un ArrayList<DetalleVenta>. Siguiendo la analogía de la caja dentro de otra caja: te abre la venta y te entrega la lista exacta de qué productos se llevó el cliente y cuántas piezas. Se usa en VentasManager para descontar esas piezas del inventario real.

**P: ¿Dónde estamos usando el método mostrarVenta() que tiene puros System.out.println?**
**R:** En la versión final gráfica, no se usa en ningún lado. Es un método diseñado para "pruebas de unidad por consola" (debugging). Lo utilizamos durante el desarrollo para verificar que la lógica matemática del negocio funcionara correctamente antes de inyectarla en la interfaz gráfica visual.


## FASE 2: LOS MOTORES (Managers)

### 1. Manejador de Usuarios (UsuarioManager.java)

**P: ¿Por qué en existeUsuario() usamos equalsIgnoreCase y en eliminarUsuario() usamos equals()?**
**R:** En existeUsuario() queremos evitar duplicados (no queremos que alguien registre 'admin' y otro 'ADMIN'). Al ignorar mayúsculas, bloqueamos variaciones del mismo nombre. En cambio, eliminarUsuario() es una acción destructiva, por lo que usamos equals() para exigir precisión milimétrica y asegurar que el admin tecleó exactamente a quién quiere borrar.

**P: ¿Cómo funciona StringBuilder y por qué es mejor que usar el signo '+' para unir textos?**
**R:** En Java los String son inmutables (no se pueden editar). Si usamos '+' para unir a 1,000 usuarios, Java destruye y crea 1,000 objetos nuevos en la memoria RAM, volviendo lento el sistema. StringBuilder funciona como un lienzo en blanco mutable; append() solo pinta más texto al final sin destruir nada. Al final, toString() nos entrega el texto final una sola vez. Es una optimización masiva de memoria.

**P: ¿Qué significa el %-15s en el String.format del StringBuilder?**
**R:** Funciona exactamente como un printf en C. La 's' significa String (texto). El número '15' significa que queremos que ese texto ocupe un espacio forzoso de 15 caracteres (rellenando con espacios en blanco si sobra espacio). Y el signo '-' significa que queremos que el texto se alinee a la izquierda. Esto nos sirve para crear columnas perfectamente derechas en la interfaz gráfica.

**P: ¿Por qué el método login() no retorna un booleano, sino un objeto Usuario?**
**R:** Porque la interfaz gráfica (MainFrame) necesita saber *quién* acaba de entrar. Dependiendo del objeto que retorne el login, MainFrame lee su Rol y decide qué botones esconder o mostrar (por ejemplo, ocultar el panel de usuarios a los empleados).

**P: ¿Qué pasa en cargarDatos() si se borra el archivo usuarios.txt por accidente?**
**R:** El FileReader arrojará un error porque el archivo no existe. Sin embargo, nuestro bloque try-catch captura esa excepción y evita que el programa colapse. Al terminar de cargar, el constructor de UsuarioManager verifica si la lista quedó vacía. Si está vacía, el sistema auto-genera un Usuario Admin por defecto (usuario: admin, pass: 1234) para garantizar que nunca nos quedemos afuera del sistema.

**P: ¿Cómo transforma cargarDatos() el texto del .txt a objetos vivos de Java?**
**R:** Lee el archivo línea por línea usando BufferedReader. Si la línea no está vacía, usa el método split(",") para romper el texto cada que encuentra una coma, guardando los pedazos en un arreglo (datos[0] es el ID, datos[1] el nombre, etc.). Finalmente, lee la posición del rol, y mediante un if/else, decide si instanciar un objeto de la clase Admin, Empleado o Cliente usando polimorfismo, para luego agregarlo a la lista de usuarios en la RAM.


### 2. Manejador de Inventario (InventarioManager.java)

**P: Si se borra usuarios.txt, el bloque catch crea un Admin y llama a guardarDatos(). ¿Esa función crea el archivo .txt de la nada?**
**R:** (Nota de UsuarioManager) ¡Sí, exactamente! En Java, la clase FileWriter tiene la instrucción de que, si el archivo que le pides no existe en la carpeta, lo crea desde cero automáticamente antes de empezar a escribir.

**P: ¿Por qué el InventarioManager tiene dos rutas de archivos (inventario.dat y inventario_default.txt)?**
**R:** Esto es un sistema de persistencia híbrida con respaldo. Nuestro archivo principal es el binario (.dat). Sin embargo, la primera vez que se instala el programa, el .dat no existe. El bloque catch atrapa ese error y recurre al 'inventario_default.txt' como semilla. Lee los productos básicos de ahí, los sube a la memoria RAM, e inmediatamente genera el 'inventario.dat' para usarlo de ahí en adelante.

**P: ¿Cómo funcionan FileInputStream y ObjectInputStream a diferencia de FileReader?**
**R:** FileReader sirve para leer texto humano, línea por línea, separando por comas. En cambio, FileInputStream y ObjectInputStream leen flujos de bytes puros (información congelada). Con una sola línea de código (lector.readObject()), Java resucita el ArrayList completo con todos los productos adentro, sin necesidad de hacer conversiones manuales.

**P: En eliminarProducto(), ¿por qué solo haces p.setActivo(false) en lugar de borrarlo con listaProductos.remove(p)?**
**R:** Porque esto es una "Baja Lógica". Si borramos el objeto Producto por completo de la memoria, podríamos corromper el historial de ventas pasadas que hacían referencia a ese producto. Al solo cambiarle la etiqueta a 'inactivo', conservamos la integridad de los datos financieros viejos, pero le damos la señal a la GUI de que ya no lo muestre en la tienda.


**P: ¿Cómo se integra el método modificarProducto(String id, int opcionCampo, String nuevoValor) con los JOptionPane de la GUI?**
**R:** Funciona como un "traductor y validador" entre la interfaz y el disco duro. 
1. La Interfaz Gráfica (PanelInventario) usa ventanas emergentes (JOptionPane) para preguntarle al usuario tres cosas: el ID del producto, QUÉ quiere cambiar (ej. 1=Nombre, 2=Precio), y el NUEVO VALOR.
2. Todo lo que el usuario teclea en un JOptionPane llega como Texto (String), incluso si tecleó "50.50". La GUI le avienta esos datos crudos al Manager.
3. El Manager recibe la petición, busca el producto, y mediante un 'switch (opcionCampo)' sabe qué atributo editar.
4. Aquí entra el try-catch: Si eligieron cambiar el precio (opción 2), el Manager intenta convertir el texto "50.50" a un decimal real usando Double.parseDouble().
5. Si el usuario se equivocó y tecleó "Cincuenta" en el JOptionPane, la conversión explota. El catch captura la explosión, evita que el programa se cierre, y retorna 'false', dándole la señal a la GUI de que muestre un mensaje de "Error: Ingresaste letras en vez de números".


### 3. Manejador de Ventas (VentasManager.java)

**P: En el constructor recibes el InventarioManager. ¿Por qué haces eso y cómo se llama este concepto?**
**R:** Esto se llama "Inyección de Dependencias". VentasManager no sabe leer archivos binarios ni sabe de existencias, su única responsabilidad son las ventas. Al inyectarle el InventarioManager en el constructor, le damos un puente de comunicación. Así, cuando va a procesar una venta, usa inventarioRef.reducirStock() delegando la responsabilidad y manteniendo un bajo acoplamiento.

**P: Veo que en guardarDatos() usas TRES archivos .txt distintos (clientes, ventas, detalles). ¿Por qué no guardas todo en uno solo?**
**R:** Porque implementamos un diseño que simula una Base de Datos Relacional (como SQL). 
- 'clientes.txt' guarda las cuentas.
- 'ventas.txt' guarda los tickets y usa el ID del cliente como Llave Foránea para saber de quién es.
- 'detalles_ventas.txt' guarda los productos y usa el ID de la Venta como Llave Foránea. 
Si metiéramos todo en un solo archivo, sería imposible mantener el orden de la "matrioska".

**P: Entonces, ¿cómo reconstruyes todo en cargarDatos() al abrir el programa?**
**R:** Hacemos una especie de 'SQL JOIN' manual. 
1. Primero leemos clientes y creamos las cuentas vacías.
2. Luego leemos las ventas, buscamos a qué cuenta pertenecen mediante el ID, y se las metemos vacías.
3. Al final leemos los detalles, buscamos la venta global que tenga ese mismo ID, buscamos el Producto en el InventarioManager, y rellenamos el detalle. ¡Es un rompecabezas perfecto!

**P: ¿Cómo funciona el Corte de Caja?**
**R:** El método generarCorteCaja(fechaOperativa) recorre TODO el historial de ventas del casino, pero tiene una condicional 'if' que solo deja pasar a las ventas que coincidan con la fecha recibida. Las va sumando y formateando con un StringBuilder. Al final, crea un archivo de texto dinámico cuyo nombre incluye la fecha (ej. Corte_Dia_02-04-2026.txt) para dejar una evidencia física de la auditoría de ese día.


**P: Explícame paso a paso cómo funciona el método procesarVenta(Venta nuevaVenta).**
**R:** Funciona en 4 pasos como una transacción bancaria segura:
1. Busca al cliente; si no existe, cancela.
2. BUCLE PREVENTIVO: Recorre todos los productos del ticket e interroga al InventarioManager: "¿Tienes suficiente stock de esto?". Si UN SOLO producto no tiene stock, cancela la venta entera y no cobra nada (evita ventas a medias o stock negativo).
3. BUCLE DE EJECUCIÓN: Como la verificación pasó limpiamente, vuelve a recorrer los productos y ahora sí, manda la orden oficial de reducir el inventario.
4. Por último, anexa la venta al historial del cliente y manda a guardar todo a los archivos de texto.

**P: Explícame paso a paso cómo funciona el método guardarDatos() en esta clase.**
**R:** Se encarga de "aplanar" nuestra estructura 3D (la Matrioska) hacia 3 archivos planos simulando tablas SQL. Utiliza 3 ciclos 'for' anidados:
- 1er FOR (Clientes): Entra a la lista global. Agarra un cliente y guarda su ID y Nombre en clientes.txt.
- 2do FOR (Ventas): Estando dentro de ese cliente, saca todos sus tickets de venta. Guarda cada ticket en ventas.txt, pero le pega el ID del cliente al lado para saber que es suyo (Llave Foránea).
- 3er FOR (Detalles): Estando dentro de ese ticket, saca los productos individuales. Los guarda en detalles_ventas.txt pegándoles el ID de la Venta al lado (Llave Foránea).
Finalmente cierra los tres archivos, dejando todo respaldado y listo para reconstruirse en el futuro.


**P: ¿Cómo funciona el método cargarDatos() para reconstruir la información al abrir el programa?**
**R:** Funciona exactamente como armar un rompecabezas en 3 fases obligatorias (de lo más general a lo más específico):
1. **Fase Clientes:** Lee `clientes.txt` y crea todas las cuentas vacías en la memoria RAM (solo con su ID y Nombre).
2. **Fase Ventas:** Lee `ventas.txt`. Por cada renglón, saca el ID del cliente que compró, busca la cuenta en la RAM (usando `buscarCliente()`) y le inserta el Ticket de Venta. Hasta este punto, los tickets existen pero están vacíos por dentro (sin productos).
3. **Fase Detalles:** Lee `detalles_ventas.txt`. Saca el ID del Producto y el ID de la Venta. Primero, va al InventarioManager a buscar el objeto del producto físico. Luego, usa la función `buscarVentaGlobal()` para escanear a tooooodos los clientes hasta encontrar exactamente el ticket con ese ID. Finalmente, le inyecta el producto y la cantidad a ese ticket. 
¡Y así la "Matrioska" queda armada perfectamente igual a como estaba antes de cerrar el programa!


### 4. Manejador de Fecha (FechaManager.java)

**P: ¿Por qué necesitamos un FechaManager en lugar de solo leer el reloj de la computadora?**
**R:** Porque necesitamos simular el paso del tiempo a nuestra voluntad. Si usáramos el reloj real de la compu, tendríamos que esperar 24 horas reales para probar si el "Corte de Caja Diario" funciona. Al tener un archivo `fecha_sistema.txt`, el administrador puede "avanzar el día" con un botón para hacer pruebas de reportes financieros del mes completo en 5 minutos.

**P: ¿Cómo avanza el día matemáticamente en Java?**
**R:** Toma el texto (ej. "01/04/2026") y usa un DateTimeFormatter para convertirlo a un objeto `LocalDate` real. Al ser un objeto de fecha, podemos invocar `.plusDays(1)` para que Java haga la matemática (sabe cuántos días tiene cada mes y años bisiestos). Luego lo vuelve a convertir a texto y lo guarda.

**P: ¿Por qué todos sus métodos y variables tienen la palabra `static`?**
**R:** Porque la fecha es una 'Verdad Universal' para todo el Casino. No necesitamos instanciar `new FechaManager()` múltiples veces. Al hacerlo `static`, la fecha vive globalmente en la memoria y cualquier parte del código puede preguntar qué día es simplemente llamando a `FechaManager.getFechaActual()`.

### 5. Manejador de Bitácoras (LogManager.java)

**P: En el método escribirLinea() usas `new FileWriter(nombreArchivo, true)`. ¿Para qué es ese 'true'?**
**R:** Ese parámetro es la clave de todo el LogManager. Por defecto, FileWriter borra el archivo y lo sobrescribe desde cero. Al ponerle `true` (modo Append), le estamos diciendo: "No borres nada, solo ve al final del documento y añade este nuevo renglón". Esto nos permite construir un historial continuo e intocable (Bitácora/Auditoría) de todos los movimientos sospechosos de los empleados.


### PROFUNDIZACIÓN: LIBRERÍA java.time (Si el profe te pregunta por código no visto en clase)

**P: En avanzarDia() de FechaManager, usas la librería java.time. Explícame línea por línea qué hace.**
**R:** Como el profe no enseñó esta librería, esto le va a encantar porque demuestra investigación:
1. `DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");` 
   -> Crea una "plantilla" para decirle a Java que nuestro texto está en formato Día/Mes/Año.
2. `LocalDate fecha = LocalDate.parse(getFechaActual(), formatter);` 
   -> Agarra el texto (ej. "28/02/2026"), lo pasa por la plantilla, y lo convierte en un "Objeto de Tiempo Real" en Java.
3. `fecha = fecha.plusDays(1);` 
   -> ¡Esta es la magia! Como ya es un objeto de tiempo, Java hace las matemáticas. Sabe perfectamente que el siguiente día no es el 29, sino el "01/03/2026" (calcula años bisiestos y fines de mes automáticamente).
4. `fechaActual = fecha.format(formatter);` 
   -> Toma ese objeto matemático y lo vuelve a convertir en un simple texto String para poder guardarlo en nuestro bloc de notas.

**P: ¿Y cómo funciona obtenerFechaHora() en LogManager?**
**R:** Es parecido, pero para el tiempo real:
1. `LocalDateTime ahora = LocalDateTime.now();` 
   -> Va y le pregunta directamente a la tarjeta madre de la computadora la fecha y la hora exacta (hasta los milisegundos) en este instante.
2. `DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");` 
   -> Crea una plantilla visual de Año-Mes-Día y Horas:Minutos:Segundos.
3. `return ahora.format(formato);` 
   -> Convierte esa hora cruda de la computadora en un texto hermoso y legible para pegarlo en nuestra bitácora como evidencia.
