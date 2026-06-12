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
