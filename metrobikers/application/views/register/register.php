<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/validators.js"></script>
        <title>Diventa ECOmmuter!</title>
    </head>

    <body>

        <div class="row">
            <div class="col-md-12">
                <?php $this->load->view('templates/visualheader'); ?> 
            </div>
        </div>

        <div class="row">
            <div class="col-md-4">
                
            </div>

            <div class="col-md-4">
                <?php echo validation_errors(); ?>
                <div class="container">
                    <h2 class="text-center">Stai per diventare un ECOmmuter!</h2>
                    <?php echo form_open('register/register') ?>
                    <div class="form-group">
                        <div class="form-group">
                            <label for="name">Nome</label>
                            <input type="input" name="name" class="required form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="surname">Cognome</label>
                            <input type="input" name="surname" class="required form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="mail">E Mail</label>
                            <input type="input" name="mail" id="mail" class="required mailinput
                                   form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="gender">Genere</label>
                            <select class="required form-control" id="gender" name="gender">
                                <option value="0">Non specificato</option>
                                <option value="1">Femmina</option>
                                <option value="2">Maschio</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="birthdate">Data di nascita</label>
                            <input type="input" name="birthdate" class="dateinput form-control"
                                   id="birthdate" class="required form-control"/>
                        </div>
                        <div class="form-group">
                            Codice di controllo:
                            <?php echo $cap['image']; ?><br/>
                            <label for="captcha">Inserisci qui il codice di controllo</label>
                            <input type="text" name="captcha" id="captcha" class="required form-control" value="" />
                        </div>
                        <input type="submit" name="submit" id="submit" value="Diventa un ECOmmuter!" class="btn btn-default form-control" />
                    </div>
                    </form>
                </div>
            </div>
        </div>
        
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>