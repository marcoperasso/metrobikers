<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title></title>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/jquery.complexify.js"></script>
        <script type="text/javascript" src="<?php echo base_url() ?>asset/js/md5-min.js"></script>
        <script type="text/javascript" >
            function getInvalidFields()
            {
                var pwdLength = 6;
                var invalids = $(".required").filter(function() {
                    return this.value === "";
                });
                if ($('#password1').val().length < pwdLength) {
                    invalids = invalids.add($('#password1'));
                }
                if ($('#password2').val().length < pwdLength) {
                    invalids = invalids.add($('#password2'));
                }
                else if ($('#password1').val() !== $('#password2').val()) {
                    invalids = invalids.add($('#password2'));
                }
                return invalids;
            }

            $(function() {

                $('#submitbtn').click(function(e) {
                    e.preventDefault();
                    var invalids = getInvalidFields();
                    if (invalids.length > 0) {
                        $(invalids[0]).focus();
                        invalids.addClass('invalid');
                        alert("Wrong password.");
                    }
                    else
                    {
                        $.get("<?php echo base_url() ?>crypt", null, function(data) {
                            eval(data);
                            var pwd = hex_md5($('#password1').val());
                            pwd = this.crypt(pwd);
                            $('#password').val(pwd);
                            $('#password1').val("");
                            $('#password2').val("");
                            $("#completeForm").submit();
                        }, "text");
                    }
                });

                $('input').change(function() {
                    $(this).removeClass('invalid');
                });

                // Use the complexify plugin on the first password field
                $('#password1').complexify({
                    minimumChars: 6,
                    strengthScaleFactor: 0.7
                },
                function(valid, complexity) {
                    $("#pwdmeter").css({"width": complexity + '%'});
                });
            });

        </script>
        <style type="text/css">
            div.pwdmeter{
                background-color: green;
                width:0%;
                height: 100%;
            }
            div.pwdmetercontainer{
                width:100%;
                height: 10px;
                background-color: red;
            }
        </style>
    </head>

    <body>
        <?php $this->load->view('templates/publicvisualheader'); ?>
        <div class="row">
            <div class="col-md-3">
                
            </div>

            <div class="col-md-6">
                <div class="container">
                    <br />
                    <h2 class="text-center">Completa la registrazione</h2><br />
                    <?php echo validation_errors(); ?>
                    <div class="col-md-2">
                
                    </div>
        
                    <div class="col-md-8">
                        <?php echo form_open('register/activate',  array('id' => 'completeForm')) ?>
                            <div class="col-md-3">
                
                            </div>
                
                            <div class="col-md-6">
                                <div class="text-center">
                                    <div class="form-group">
                                        <input type="password" name="password1" id="password1" class="required form-control" placeholder="Scegli una password"/>
                                    </div>
                                    <div class="form-group">
                                        <input type="password" name="password2" id="password2" class="required form-control" placeholder="Ripeti la password password"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="pwdmeter">Sicurezza della password:</label>
                                        <div  class="pwdmetercontainer">
                                            <div id ="pwdmeter" class="pwdmeter">
                                                
                                            </div>
                                        </div>
                                    </div>
                        
                                    <input id="userkey" name="userkey" type="hidden" value="<?php echo $key;?>"/>
                                    <input id="password" name="password" type="hidden" />
                                    <input type="submit" name="submitbtn" id="submitbtn" value="Completa la registrazione" class="btn btn-default form-control"/> 
                                </div>
                            </div>
                            <div class="col-md-3">
                                
                            </div>
                        </form>
                    </div>
                    <div class="col-md-2">
                        
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                
            </div>
            
        </div>
    <?php $this->load->view('templates/footer'); ?>
</body>
</html>