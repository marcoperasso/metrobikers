<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="row">
            <div class="col-md-5">
            </div>
            <div class="col-md-6">
                <!-- Button trigger modal -->
                <?php $this->load->view('login'); ?>
                <a data-toggle="modal" href="#loginModal" data-backdrop="static"><p class="text-right">Accedi</p></a>
            </div>
            <div class="col-md-1">
            </div>
        </div>
        <div class="row body">
            <!-- <div class="col-md-1">
               <a href="<?php echo base_url() ?>home/mission" class="btn btn-default btn-lg">La nostra filosofia</a>
                <br />
                <a href="/asset/other/ECOmmutersMobile.apk" >
                    Scarica la App per Android 
                </a>
            </div>-->
            <div class="col-md-5">
                <a href="<?php echo base_url() ?>" title="Vai alla pagina principale">
                    <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo">
                </a>
            </div>
            <div class="col-md-6">
                <div class="jumbotron">
                    <div class="container">
                        <h1><b>Benvenuto in ECOmmuters!</b></h1>
                            <p>Condividi i tuoi spostamenti con altre persone,<br />
                             cerca nuovi compagni di viaggio con cui condividere i tuoi spostamenti.</p>
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
    </body>
</html>
