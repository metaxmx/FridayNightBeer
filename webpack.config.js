var webpack = require('webpack');
var path = require('path');
var ExtractTextPlugin = require("extract-text-webpack-plugin");

// Webpack Config
var webpackConfig = {
    entry: {
        'polyfills':       './frontend/bundle/polyfills.ts',
        'vendor':          './frontend/bundle/vendor.ts',
        'app':             './frontend/bundle/app.ts',
        'vendor-style':    './frontend/bundle/vendor-style.ts',
        'app-style':       './frontend/bundle/app-style.ts'
    },

    devtool: 'source-map',

    cache: true,
    output: {
        path: './public/dist',
        filename: '[name].bundle.js',
        sourceMapFilename: '[name].map',
        chunkFilename: '[id].chunk.js'
    },

    resolve: {
        extensions: ['', '.ts', '.js']
    },

    plugins: [
        new webpack.optimize.CommonsChunkPlugin({ name: ['app', 'vendor', 'polyfills'], minChunks: Infinity }),
        new ExtractTextPlugin("[name].css"),
        new webpack.optimize.DedupePlugin()
    ],

    module: {
        loaders: [
            // .ts files for TypeScript
            { test: /\.ts$/, loader: 'ts-loader' },
            { test: /\.css$/, loader: ExtractTextPlugin.extract("style-loader", "css-loader") },
            { test: /\.less$/, loader: ExtractTextPlugin.extract("style-loader", "css-loader!less-loader") },
            { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: "file" },
            { test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader:"url?prefix=font/&limit=5000" },
            { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&mimetype=application/octet-stream" },
            { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&mimetype=image/svg+xml" }
        ]
    }

};

var webpackMerge = require('webpack-merge');
module.exports = webpackMerge(webpackConfig);