<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="container">

            <?php $this->load->view('templates/visualheader'); ?>

            <div class="row body">
                <div class="col-md-12">
                    <h3>I miei dati</h3>
                    email: <?php echo $user->mail; ?><br/>
                    nome: <?php echo $user->name; ?><br/>
                    cognome: <?php echo $user->surname; ?><br/>
                    data di nascita: <?php echo $user->birthdate; ?><br/>
                    sesso: <?php echo $user->gender; ?><br/>
                </div>
		<?php
		$data = isset($routes) ? array('routes'=> $routes) : array();
		$this->load->view('map', $data);
		?>
            </div>

            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>
