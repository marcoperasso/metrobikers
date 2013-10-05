<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Arrivederci a presto</title>
    </head>

    <body>
        <div class="container">
            <div class="row header">
                <div class="col-md-12">
                    <div class="row">
                        <div class="col-md-3">

                        </div>
                        <div class="col-md-6">
                            <a href="base_url()" title="Vai alla pagina principale">
                                <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
                            </a>
                        </div>
                        <div class="col-md-3">
                           
                        </div>
                    </div>

                </div>
            </div>
            <div class="row body">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <p>Sei uscito con successo da ECOmmuters, a presto!</p>
                </div>
                <div class="col-md-1"></div>
            </div>
        </div>               
    </body>
</html>
