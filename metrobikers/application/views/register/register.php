<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/validators.js"></script>
        <title>Diventa ECOmmuter!</title>
    </head>

    <body>
        <div class="row">
            <div class="col-md-5">
            </div>
            <div class="col-md-6">
               
            </div>
            <div class="col-md-1">
            </div>
        </div>
            <div class="row body">
                <div class="col-md-5">
                    <a href="<?php echo base_url() ?>" title="Vai alla pagina principale">
                        <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo">
                    </a>
                </div>

                <div class="col-md-6">
                    <?php echo validation_errors(); ?>
                    <div class="container">
                        <br />
                        <h2 class="text-center">Stai per diventare un ECOmmuter!</h2><br />
                        <?php echo form_open('register/register') ?>
                        <div class="col-md-1">

                        </div>

                        <div class="col-md-10">
                            <div class="form-group">
                                <div class="form-group">
                                    <input type="input" name="name" class="required form-control" placeholder="Nome"/>
                                </div>
                                <div class="form-group">
                                    <input type="input" name="surname" class="required form-control" placeholder="Cognome"/>
                                </div>
                                <div class="form-group">
                                    <input type="input" name="mail" id="mail" class="required mailinput
                                           form-control" placeholder="Email"/>
                                </div>
                                <br /><br />
                                <input type="submit" name="submit" id="submit" value="Diventa un ECOmmuter!" class="btn btn-primary form-control" />
                                <br /><br />
                            </div>
                        </div>
                        <div class="col-md-1">

                        </div>
                        </form>
                    </div>
                </div>
                <div class="col-md-1">

                </div>
            </div>

            <?php $this->load->view('templates/footer'); ?>
    </body>
</html>