const path = require('path');
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: './app/app.js',
    mode: 'development',
    devtool: 'source-map',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'app.js'
    },
    devServer: {
        contentBase: './app',
        watchContentBase: true,
        port: 3000,
        hot: true,
        overlay: true
    },
    plugins: [
        new CopyPlugin([
            { from: 'app/index.html', to: './' },
            { from: 'app/app.css', to: './' },
            { from: 'app/bpmnio.css', to: './' },
            { from: 'app/diff.css', to: './' },
        ]),
    ],
};