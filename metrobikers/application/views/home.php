<?php
$user = get_user();
if ($user != NULL) {
        redirect(base_url() . 'user');
} ?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class ="content">
            <div class="row header">
                <div class="col-md-11">
                    <!-- Button trigger modal -->
                    <?php $this->load->view('login'); ?>
                    <a data-toggle="modal" href="#loginModal" data-backdrop="static"><p class="text-right" style="padding-right: 30px">Accedi</p></a>
                </div>
                <div class="col-md-1">
                </div>
            </div>
            <div class="row body">
                <div class="col-md-1"></div>
                <div class="col-md-4">
                    <a href="<?php echo base_url() ?>" title="Vai alla pagina principale">
                        <img class="logo" src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo">
                    </a>
                </div>
                <div class="col-md-6">
                    <div class="jumbotron">
                        <div class="container">
                            <h1><b>Benvenuto in ECOmmuters!</b></h1>
                            <p>Cerca nuovi compagni di viaggio con cui condividere i tuoi spostamenti <i><b>E</b></I>-cologici.</p>
                            <p>
                                <a href="<?php echo base_url() ?>register" class="btn btn-primary btn-lg">Diventa anche tu un ECOmmuter!</a>
                                <a href="<?php echo base_url() ?>home/mission" class="btn btn-default btn-lg">Cos'è sta roba?</a>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-1">
                </div>
            </div>
            <?php $this->load->view('templates/footer'); ?>              
        </div>
    </body>
</html>
