<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>User activated</title>
    </head>

    <body>
        <?php $this->load->view('templates/publicvisualheader'); ?>
        <h1>User activated</h1>
        <p>Congratulations, <?php echo $user->name . ' ' . $user->surname?>, your registration is now active, welcome to the ECOmmuters community!</p>
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>