<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title></title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <h1>Invalid activation key</h1>
        <p>Invalid activation key: <?php echo $key?></p>
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>