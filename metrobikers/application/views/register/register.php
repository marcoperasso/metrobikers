<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/register.js"></script>
        <style>
            input, select{
                width:200px;
                display: inline-block;
            }
        </style>
        <title>Register user</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <h1>Register user</h1>
        <?php echo validation_errors(); ?>

        <?php echo form_open('register/register') ?>
        <div class="centercontent">
            <label for="name">First Name</label> 
            <input type="input" name="name" class="required"/><br />
            <label for="surname">Last Name</label> 
            <input type="input" name="surname"  class="required"/><br />
            <label for="mail">E Mail</label> 
            <input type="input" name="mail" id="mail" class="required"/><br />
            <label for="gender">Gender</label> 
            <select class="required" id="gender" name="gender">
                <option value="0">Unspecified</option>
                <option value="1">Female</option>
                <option value="2">Male</option>
            </select><br />
            <label for="birthdate">Birth date</label> 
            <input type="input" name="birthdate" id="birthdate" class="required"/><br />
            <input type="submit" name="submit" id="submit" value="Register" /> 
        </div>
    </form>
    <?php $this->load->view('templates/footer'); ?>
</body>
</html>