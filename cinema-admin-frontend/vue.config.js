
module.exports = {
    devServer: {
        port: 8587
    },
    publicPath:process.env.NODE_ENV === 'production' ? '/电影院后台管理' : '/'
}
