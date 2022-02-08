<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>词汇表</title>
    <style>
        table {
            border: 1px black solid;
            border-collapse: collapse;
        }

        th, td {
            border: 1px black solid;
        }

        .width-2 {
            width: 17%;
        }

        .width-8 {
            width: 66%;
        }

        .pronounce-block {
            cursor: pointer;
            display: inline-block;
            margin-right: 5px;
        }
    </style>
</head>
<body>
<script>
    function audioPlay(e) {
        if (e.firstChild.src !== '' && e.firstChild.pause) {
            e.firstChild.play();
        }
    }
</script>
<table>
    <tr>
        <th class="width-2">单词</th>
        <th class="width-2">音标</th>
        <th class="width-8">解释</th>
    </tr>
    <#list vocabularyItems as item>
        <tr>
            <td>${item.name}</td>
            <td>
                <span class="pronounce-block" onclick="audioPlay(this)"><#if item.ukSpeak??><audio
                    src="${item.ukSpeak}"></audio></#if>英:[${item.ukPronounce!}]</span>
                <span class="pronounce-block" onclick="audioPlay(this)"><#if item.ukSpeak??><audio
                    src="${item.usSpeak}"></audio></#if>美:[${item.usPronounce!}]</span>
            </td>
            <td>${item.translate!}</td>
        </tr>
    </#list>
</table>
</body>
</html>