<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title><title><?=$page_title?></title></title>
    </head>

    <body>
        <div class="content">
            <?php $this->load->view('templates/visualheader'); ?>
            <div class="body">
                <div><?=$view_content?></div>
            </div>
            <?php $this->load->view('templates/footer'); ?>
        </div>
    </body>
</html>