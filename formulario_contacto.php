{\rtf1\ansi\ansicpg1252\deff0\nouicompat\deflang3082{\fonttbl{\f0\fnil\fcharset0 Courier New;}}
{\*\generator Riched20 10.0.18362}\viewkind4\uc1 
\pard\f0\fs22 <?php\par
\par
    $nombre = $_GETT['xnombre'];\par
    $apellido = $_GET['xapellido'];\par
    $correo = $_GET['xcorreo'];\par
\par
    if (strpos(("@", $correo) != false)\{\par
        include 'test.html';\par
\tab    echo "Informaci\'f3n de Contacto";\par
\tab    echo "Nombre: ", $nombre\par
        echo "Apellido: ", $apellido\par
        echo "E-mail: ", $correo\par
    \}else\{\par
        echo "Correo inv\'e1lido";\par
    \}\par
?>\par
}
 