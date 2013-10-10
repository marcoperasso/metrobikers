<?php $this->load->view('templates/redirectnotlogged'); ?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="content">

            <?php
	    $data['url'] = base_url()."user";
	    $this->load->view('templates/visualheader', $data);
	    ?>

            <div class="row body">
                <div class="col-md-12">
                    <h3>I miei dati</h3>
                    email: <?php echo $user->mail; ?><br/>
                    nome: <?php echo $user->name; ?><br/>
                    cognome: <?php echo $user->surname; ?><br/>
                    data di nascita: <?php echo $user->birthdate; ?><br/>
                    sesso: <?php echo $user->gender; ?><br/>
                </div>
            </div>

            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>
