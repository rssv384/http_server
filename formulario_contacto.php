<?php
    $nombre = $_GET['xnombre'];
    $apellido = $_GET['xapellido'];
    $correo = $_GET['xcorreo'];

    if (strpos(("@", $correo) != false){
        include 'test.html';
        echo "Informaci\'f3n de Contacto";
        echo "Nombre: ", $nombre
        echo "Apellido: ", $apellido
        echo "E-mail: ", $correo
    }else{
        echo "Correo inválido";
    }
?
