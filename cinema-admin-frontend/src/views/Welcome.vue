
<template>
    <div id="welcome">
        <div id="bk"></div>
    </div>
</template>

<script>
export default {
    name: "Welcome",
};
</script>

<style scoped>
#welcome {
    background: url("../assets/welcome.jpg");
    background-size: cover;
    height: 100%;
    width: 100%;
}
#bk {
    color: #000000;
    margin: 0;
    text-align: left;
    font-size: 36px;
    padding-top: 20px;
}
</style>
