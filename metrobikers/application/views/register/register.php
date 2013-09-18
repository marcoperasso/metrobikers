<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/validators.js"></script>
        <style>
            input, select{
                width:200px;
                display: inline-block;
            }
        </style>
        <title>Diventa ECOmmuter!</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <h1>Stai per diventare un ECOmmuter!</h1>
        <?php echo validation_errors(); ?>

        <?php echo form_open('register/register') ?>
        <div class="text-center">
            <label for="name">Nome</label> 
            <input type="input" name="name" class="required"/><br />
            <label for="surname">Cognome</label> 
            <input type="input" name="surname"  class="required"/><br />
            <label for="mail">E Mail</label> 
            <input type="input" name="mail" id="mail" class="required mailinput"/><br />
            <label for="gender">Genere</label> 
            <select class="required" id="gender" name="gender">
                <option value="0">Non specificato</option>
                <option value="1">Femmina</option>
                <option value="2">Maschio</option>
            </select><br />
            <label for="birthdate">Data di nascita</label> 
            <input type="input" name="birthdate" class="dateinput" id="birthdate" class="required"/><br />
            Codice di controllo:
            <?php echo $cap['image']; ?><br />
            <label for="captcha">Inserisci qui il codice di controllo</label> 
            <input type="text" name="captcha" id="captcha" class="required" value="" /><br />
            <input type="submit" name="submit" id="submit" value="Registrati" /> 
        </div>
    </form>
    <?php $this->load->view('templates/footer'); ?>
</body>
</html>