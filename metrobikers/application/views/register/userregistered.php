<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>User registered</title>
    </head>
    
    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <h1>User registered</h1>
        <p>Thank you <?php echo $user->name." ".$user->surname?> for registering to ECOmmuters; your account needs to be activated, 
            please follow the instructions we sent to your email address <?php echo $user->mail?>.</p>
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>