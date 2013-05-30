<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Register user</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <h1>Register user</h1>
        <?php echo validation_errors(); ?>

        <?php echo form_open('register/register') ?>

        <label for="firstname">Firt Name</label> 
        <input type="input" name="firstname" /><br />
        <label for="lastname">Last Name</label> 
        <input type="input" name="lastname" /><br />
        <label for="email">E Mail</label> 
        <input type="input" name="email" /><br />

        <input type="submit" name="submit" value="Register" /> 

    </form>
    <?php $this->load->view('templates/footer'); ?>
</body>
</html>