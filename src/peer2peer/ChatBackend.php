<?php
    $dbhost = "mysql12.000webhost.com";
    $dbname = "a7162677_Chat";
    $dbuser = "a7162677_Chat";
    $dbpass = "black1";

    mysql_connect($dbhost, $dbuser, $dbpass) or die(mysql_error());
    mysql_select_db($dbname) or die(mysql_error());

    $option = $_POST['Option'];
    $nickname = $_POST['nickname'];
    $identifier = $_POST['identifier'];
    $isOn = $_POST['isOn'];
    $ip = $_POST['ip'];
    $port = $_POST['port'];

    $message = $_POST['message'];
    $empfaenger = $_POST['empfaenger'];

    if ($option == "UPLOAD") {
        $result = "";
        if (mysql_num_rows( mysql_query("SELECT * FROM UserData WHERE IDENTIFIER LIKE '$identifier' ")) == 0) {
            echo("First");
            $result = mysql_query("INSERT INTO UserData (IDENTIFIER, Nickname) VALUES('$identifier', '$nickname')");
        } else {
            echo("Second");
            echo $nickname;
            $result = mysql_query("UPDATE UserData SET Nickname='$nickname' WHERE IDENTIFIER='$identifier'");
        }
        echo (mysql_num_rows( mysql_query("SELECT * FROM UserData WHERE IDENTIFIER LIKE '$identifier'")));
        echo($result);
    } else if ($option == "LOAD") {
        $result = mysql_query("SELECT IDENTIFIER, IP, PORT, MAX(DATE) FROM UserData Group By IDENTIFIER, IP, PORT");
        while ($line = mysql_fetch_array($result)) {
                echo $line[IDENTIFIER];
                echo (":");
                echo $line[IP];
                echo (":");
                echo $line[PORT];
                echo (";");
        }
        mysql_free_result($result);
    } else if ($option == "UPDATE") {
        $result = mysql_query("UPDATE UserData SET IP = '$ip', NICKNAME = '$nickname', PORT = '$port' WHERE IDENTIFIER = '$identifier'");
        echo($result);
        mysql_free_result($result);
    } else if ($option == "DELETE") {
        $result = mysql_query("DELETE FROM UserData WHERE IDENTIFIER = '$identifier'");
        echo($result);
        mysql_free_result($result);
    } else if($option == "UPMESSAGE") {
        mysql_query("INSERT INTO Messages (Nachricht, Verfasser, Empfaenger) VALUES('$message', '$identifier', '$empfaenger')");
    } else if($option == "LOADMESSAGES") {
        $result = mysql_query("SELECT Nachricht, Verfasser, Empfaenger, Datum FROM Messages WHERE Empfaenger LIKE '%$identifier%' AND Datum > (SELECT LastCheck FROM UserData WHERE UserData.Identifier LIKE '$identifier')");
        echo mysql_error ();
        $users = mysql_query("SELECT Nickname, Identifier FROM UserData");
        $userArray = array();

        while ($user = mysql_fetch_array($users)) {
            $userArray[$user[Identifier]] = $user[Nickname];
        }


        while ($line = mysql_fetch_array($result)) {
            echo ("ASDF");
            echo $line[Verfasser];
            echo ("§U§");
            echo $userArray[$line[Verfasser]];

            echo ("§2§");

            $empfaenger = explode(";", $line[Empfaenger]);
            foreach ($empfaenger as &$value) {
                echo $value;
                echo ("§U§");
                echo $userArray[$value];
                echo "§T§";
            }

            echo ("§2§");

            echo $line[Nachricht];

            echo ("§2§");

            echo $line[Date];

            echo ("§1§");
        }
    }
    mysql_close();
?>